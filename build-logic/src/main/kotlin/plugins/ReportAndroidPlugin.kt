package plugins

import com.android.build.VariantOutput.OutputType
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.getRegularFiles
import com.android.build.gradle.api.ApplicationVariant as DeprecatedApplicationVariant
import com.android.build.gradle.api.LibraryVariant as DeprecatedLibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.Directory
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.register
import java.io.File

class ReportAndroidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponent = project.extensions.getByType(AndroidComponentsExtension::class)
        val app = project.extensions.getByType<AppExtension>()

        project.afterEvaluate {

            val matchingFallbacks = app.productFlavors.associate {
                (it.dimension!! to it.name) to it.matchingFallbacks.toList()
            }


            app.applicationVariants.forEach { appVariant ->

                println(appVariant.name)


                val config = appVariant.runtimeConfiguration // либо compileConfiguration
                val allResFiles = config.incoming.artifactView {
                    attributes.attribute(
                        ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                        AndroidArtifacts.ArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME.type
                    )
                }
                    .artifacts
                    .artifactFiles

                //val resFilesProperty = project.objects.listProperty<RegularFile>()
                //resFilesProperty.addAll(allResFiles)


               /* tasks.register<Copy>("${appVariant.name}PrintRes") {
                    from(allResFiles)
                    into(layout.buildDirectory.dir("allPublicRes/${appVariant.name}"))
                    this.duplicatesStrategy = DuplicatesStrategy.INHERIT
                }*/


                project.tasks.register<PrintPublicResTask>("${appVariant.name}PrintRes") {
                    //inputs.files(allResFiles)
                    group = "verification"
                    resFiles.set(allResFiles)
                }

                val productFlavors = appVariant.productFlavors.associate {
                    it.dimension!! to it.name
                }

                project.getProjectDependencies(
                    listOf(
                        appVariant.runtimeConfiguration.name,
                        appVariant.compileConfiguration.name
                    )
                ).mapNotNull {
                    val libProject = rootProject.findProject(it)!!
                    val extension = libProject.extensions.findByType<LibraryExtension>() ?: return@mapNotNull null
                    libProject to extension
                }.forEach { (libProject, extension) ->
                    val variant = extension.findMatchingVariant(
                        productFlavors = productFlavors,
                        matchingFallbacks = matchingFallbacks,
                        buildType = appVariant.buildType.name,
                    )
                    val files = variant.runtimeConfiguration.incoming.artifactView {
                        attributes.attribute(
                            ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                            AndroidArtifacts.ArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME.type
                        )
                    }
                        .artifacts
                        .artifactFiles
                        .files

                    println("-${libProject.name}: ${variant.name}")
                }
                println()
            }
        }
    }
}

fun artifactView(appVariant: DeprecatedApplicationVariant) {
    val config = appVariant.runtimeConfiguration // либо compileConfiguration
    config.incoming.artifactView {
        attributes.attribute(
            ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
            AndroidArtifacts.ArtifactType.PUBLIC_RES.type
        )
    }
        .artifacts
        .artifactFiles
        .files
        .filter {
            !it.path.contains(".gradle")
        }
        .forEach { file ->
            println("Public resources list for ${appVariant.name}: $file")
        }
}

fun LibraryExtension.findMatchingVariant(
    productFlavors: Map<String, String>,
    matchingFallbacks: Map<Pair<String, String>, List<String>>,
    buildType: String,
): DeprecatedLibraryVariant {
    var candidates = libraryVariants.toList()
    productFlavors.forEach { (dim, value) ->
        var matchingFallbackIndex = 0
        var currentValue = value
        var filteredCandidates = emptyList<DeprecatedLibraryVariant>()
        while (filteredCandidates.isEmpty()) {
            filteredCandidates = candidates.filter { libVariant ->
                val dimensionValue = libVariant.productFlavors.find {
                    it.dimension == dim
                }
                dimensionValue == null || dimensionValue.name == currentValue
            }
            if (filteredCandidates.isEmpty()) {
                currentValue =
                    matchingFallbacks[dim to value]?.getOrNull(matchingFallbackIndex) ?: error("no matching variant")
                matchingFallbackIndex++
            } else {
                candidates = filteredCandidates
            }
        }
    }

    return candidates.find { it.buildType.name == buildType } ?: error("no matching variant")
}

fun LibraryExtension.findMatchingVariant(
    appFlavourConfig: Set<String>,
    buildType: String,
    projectName: String
): DeprecatedLibraryVariant {
    return libraryVariants.find { libraryVariant ->
        val buildTypeMatch = buildType == libraryVariant.buildType.name
        val flavourMatch = libraryVariant.productFlavors.all {
            it.name in appFlavourConfig
        }
        buildTypeMatch && flavourMatch
    }
        ?: libraryVariants.first() //error("no matching variant ${libraryVariants.map { it.name}} / $appFlavourConfig $buildType ($projectName)")
}