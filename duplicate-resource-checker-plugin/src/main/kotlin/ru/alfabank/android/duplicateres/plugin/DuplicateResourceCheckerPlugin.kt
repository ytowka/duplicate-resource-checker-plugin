package ru.alfabank.android.duplicateres.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import ru.alfabank.android.duplicateres.tasks.BaseDuplicateResourcesTask
import ru.alfabank.android.duplicateres.tasks.CreateBaselineDuplicateResourcesTask
import ru.alfabank.android.duplicateres.tasks.FindDuplicateResourcesTask
import ru.alfabank.android.duplicateres.utils.configurePlugin
import ru.alfabank.android.duplicateres.utils.findParseLocalResourceTask
import ru.alfabank.android.duplicateres.utils.getBaselineFile
import ru.alfabank.android.duplicateres.utils.getBaselineFileProvider
import ru.alfabank.android.duplicateres.utils.mapToMetadata

private const val BASE_TASK_NAME = "DuplicateResources"
private const val BASELINE_DIRECTORY = "duplicateResBaseline"
private const val ANDROID_PLUGIN_ID = "com.android.base"

public class DuplicateResourceCheckerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val pluginConfig = project.extensions.create("duplicateResourceChecker", DuplicateResourceExtension::class)
        pluginConfig.baselinePath.convention(BASELINE_DIRECTORY)

        project.configurePlugin(ANDROID_PLUGIN_ID) {
            configureTasks(project, pluginConfig)
        }
    }

    private fun configureTasks(project: Project, pluginConfig: DuplicateResourceExtension) {
        val androidComponent = project.extensions.getByType(AndroidComponentsExtension::class)
        check(androidComponent is ApplicationAndroidComponentsExtension) {
            "Plugin must be applied to application module"
        }

        androidComponent.onVariants { appVariant ->
            val config = appVariant.runtimeConfiguration
            val artifactCollection = config.incoming.artifactView {
                attributes.attribute(
                    ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                    AndroidArtifacts.ArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME.type
                )
            }
                .artifacts

            val baselinePath = pluginConfig.baselinePath.get()

            project.tasks.register<FindDuplicateResourcesTask>(
                appVariant.computeTaskName("check", BASE_TASK_NAME)
            ) {
                baseConfigure(project, artifactCollection, appVariant, pluginConfig)
                baselineFile.set(project.getBaselineFileProvider(appVariant, baselinePath))
            }

            project.tasks.register<CreateBaselineDuplicateResourcesTask>(
                appVariant.computeTaskName("baseline", BASE_TASK_NAME)
            ) {
                baseConfigure(project, artifactCollection, appVariant, pluginConfig)
                baselineFile.set(project.getBaselineFile(appVariant, baselinePath))
            }
        }
    }

    private fun BaseDuplicateResourcesTask.baseConfigure(
        project: Project,
        artifactCollection: ArtifactCollection,
        appVariant: Variant,
        extension: DuplicateResourceExtension,
    ) {
        val localResFileParseTask = project.findParseLocalResourceTask(appVariant)
        val localResourceListOutputs = localResFileParseTask.map { it.outputs.files }

        group = JavaBasePlugin.VERIFICATION_GROUP
        libraryResFiles.from(artifactCollection.artifactFiles)
        artifactMetadataList.set(artifactCollection.mapToMetadata())
        localResFiles.from(localResourceListOutputs)
        excludeResTypes.set(extension.excludeResourceType)
        appProjectPath.set(project.path)
    }
}
