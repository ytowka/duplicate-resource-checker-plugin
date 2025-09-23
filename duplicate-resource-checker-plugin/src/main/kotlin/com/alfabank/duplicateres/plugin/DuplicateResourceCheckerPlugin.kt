package com.alfabank.duplicateres.plugin

import com.alfabank.duplicateres.tasks.BaseDuplicateResourcesTask
import com.alfabank.duplicateres.tasks.CreateBaselineDuplicateResourcesTask
import com.alfabank.duplicateres.tasks.FindDuplicateResourcesTask
import com.alfabank.duplicateres.utils.buildProjectsMap
import com.alfabank.duplicateres.utils.configurePlugin
import com.alfabank.duplicateres.utils.findParseLocalResourceTask
import com.alfabank.duplicateres.utils.getBaselineFile
import com.alfabank.duplicateres.utils.getBaselineFileProvider
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

private const val BASE_TASK_NAME = "DuplicateResources"
private const val ANDROID_PLUGIN_ID = "com.android.base"

public class DuplicateResourceCheckerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val pluginConfig = project.extensions.create("duplicateResourceFinder", DuplicateResourceExtension::class)

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
            val allResFiles = config.incoming.artifactView {
                attributes.attribute(
                    ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                    AndroidArtifacts.ArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME.type
                )
            }
                .artifacts
                .artifactFiles

            project.tasks.register<FindDuplicateResourcesTask>(
                appVariant.computeTaskName("check", BASE_TASK_NAME)
            ) {
                baseConfigure(project, allResFiles, appVariant, pluginConfig)
                baselineFile.set(project.getBaselineFileProvider(appVariant))
            }

            project.tasks.register<CreateBaselineDuplicateResourcesTask>(
                appVariant.computeTaskName("baseline", BASE_TASK_NAME)
            ) {
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
        allProjectPaths.set(project.buildProjectsMap())
        appProjectPath.set(project.path)
    }
}
