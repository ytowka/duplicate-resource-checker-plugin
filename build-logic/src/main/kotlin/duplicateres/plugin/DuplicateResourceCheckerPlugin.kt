package duplicateres.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import duplicateres.tasks.BaseDuplicateResourcesTask
import duplicateres.tasks.CreateBaselineDuplicateResourcesTask
import duplicateres.tasks.FindDuplicateResourcesTask
import duplicateres.utils.findParseLocalResourceTask
import duplicateres.utils.getBaselineFile
import duplicateres.utils.getBaselineFileProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register


private const val FIND_TASK_NAME = "FindDuplicateResources"
private const val BASELINE_TASK_NAME = "CreateDuplicateResBaseline"

class DuplicateResourceCheckerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponent = project.extensions.getByType(AndroidComponentsExtension::class)

        val pluginConfig = project.extensions.create("duplicateResourceFinder", DuplicateResourceExtension::class)

        androidComponent.onVariants { appVariant ->
            val config = appVariant.runtimeConfiguration
            val allResFiles = config.incoming.artifactView {
                attributes.attribute(
                    ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                    AndroidArtifacts.ArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME.type
                )
            }
                .artifacts
                .artifactFiles


            project.tasks.register<FindDuplicateResourcesTask>("${appVariant.name}$FIND_TASK_NAME") {
                baseConfigure(project, allResFiles, appVariant, pluginConfig)
                baselineFile.set(project.getBaselineFileProvider(appVariant))
            }

            project.tasks.register<CreateBaselineDuplicateResourcesTask>("${appVariant.name}$BASELINE_TASK_NAME") {
                baseConfigure(project, allResFiles, appVariant, pluginConfig)
                baselineFile.set(project.getBaselineFile(appVariant))
            }
        }
    }

    private fun BaseDuplicateResourcesTask.baseConfigure(
        project: Project,
        allResFiles: FileCollection,
        appVariant: Variant,
        extension: DuplicateResourceExtension,
    ) {
        val localResFileParseTask = project.findParseLocalResourceTask(appVariant)
        val localResourceListOutputs = localResFileParseTask.map { it.outputs.files }

        group = JavaBasePlugin.VERIFICATION_GROUP
        libraryResFiles.from(allResFiles)
        localResFiles.from(localResourceListOutputs)
        excludeResTypes.set(extension.excludeResourceType)
    }
}