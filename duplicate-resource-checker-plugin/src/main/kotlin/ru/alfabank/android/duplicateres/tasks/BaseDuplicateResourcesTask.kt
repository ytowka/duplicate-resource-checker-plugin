package ru.alfabank.android.duplicateres.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import ru.alfabank.android.duplicateres.internal.ArtifactMetadata
import ru.alfabank.android.duplicateres.internal.ResourcesNamesStorage
import ru.alfabank.android.duplicateres.internal.addName
import ru.alfabank.android.duplicateres.plugin.ResourceType

// first lines is not resources
private const val DROP_LOCAL_FILE_LINE_COUNT = 2
private const val DROP_LIBRARY_FILE_LINE_COUNT = 1

public abstract class BaseDuplicateResourcesTask : DefaultTask() {

    @get:InputFiles
    public abstract val libraryResFiles: ConfigurableFileCollection

    @get:Internal
    public abstract val artifactMetadataList: ListProperty<ArtifactMetadata>

    @get:InputFiles
    public abstract val localResFiles: ConfigurableFileCollection

    @get:Input
    public abstract val excludeResTypes: SetProperty<ResourceType>

    @get:Input
    public abstract val appProjectPath: Property<String>

    internal fun populateResourceStorage(resourcesNamesStorage: ResourcesNamesStorage) {
        val excludeTypes = excludeResTypes.get()
            .map { it.type }
            .toSet()
        val artifactMetadata = artifactMetadataList.get()
            .associateBy(ArtifactMetadata::filePath)

        libraryResFiles.forEach { libraryResFile ->
            val artifactDisplayName = artifactMetadata[libraryResFile.path]?.displayName ?: ""
            val projectPath = artifactDisplayName.replace(" ", "")
            libraryResFile.readLines()
                .drop(DROP_LIBRARY_FILE_LINE_COUNT)
                .forEach { resLine ->
                    resourcesNamesStorage.addName(resLine, excludeTypes, projectPath)
                }
        }

        // local file is only file in collection
        val localResFile = localResFiles.first()

        localResFile.readLines()
            .drop(DROP_LOCAL_FILE_LINE_COUNT)
            .forEach { resLine ->
                resourcesNamesStorage.addName(resLine, excludeTypes, appProjectPath.get())
            }
    }
}
