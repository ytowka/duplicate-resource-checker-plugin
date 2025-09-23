package com.alfabank.duplicateres.tasks

import com.alfabank.duplicateres.internal.ResourcesNamesStorage
import com.alfabank.duplicateres.internal.addName
import com.alfabank.duplicateres.plugin.ResourceType
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles

private const val BUILD_DIRECTORY = "/build/"

// first lines is not resources
private const val DROP_LOCAL_FILE_LINE_COUNT = 2
private const val DROP_LIBRARY_FILE_LINE_COUNT = 1

public abstract class BaseDuplicateResourcesTask : DefaultTask() {

    @get:InputFiles
    public abstract val libraryResFiles: ConfigurableFileCollection

    @get:InputFiles
    public abstract val localResFiles: ConfigurableFileCollection

    @get:Input
    public abstract val excludeResTypes: SetProperty<ResourceType>

    @get:Input
    public abstract val allProjectPaths: MapProperty<String, String>

    @get:Input
    public abstract val appProjectPath: Property<String>

    internal fun populateResourceStorage(resourcesNamesStorage: ResourcesNamesStorage) {
        val excludeTypes = excludeResTypes.get()
            .map { it.type }
            .toSet()
        libraryResFiles
            // filter to exclude external libraries resource files. May be useful in future
            .filter { it.path.contains(BUILD_DIRECTORY) }
            .forEach { libraryResFile ->
                val projectDir = libraryResFile.path.substringBefore(BUILD_DIRECTORY)
                val projectPath = allProjectPaths.get()[projectDir] ?: ""
                libraryResFile
                    .readLines()
                    .drop(DROP_LIBRARY_FILE_LINE_COUNT)
                    .forEach { resLine ->
                        resourcesNamesStorage.addName(resLine, excludeTypes, projectPath)
                    }
            }

        // local file is only file in collection
        val localResFile = localResFiles.first()

        localResFile
            .readLines()
            .drop(DROP_LOCAL_FILE_LINE_COUNT)
            .forEach { resLine ->
                resourcesNamesStorage.addName(resLine, excludeTypes, appProjectPath.get())
            }
    }
}
