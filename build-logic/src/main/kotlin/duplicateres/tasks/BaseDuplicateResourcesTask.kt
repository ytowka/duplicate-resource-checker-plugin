package duplicateres.tasks

import duplicateres.utils.findProjectByResFile
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

private const val BUILD_DIRECTORY = "/build/"

// first lines is not resources
private const val DROP_LOCAL_FILE_LINE_COUNT = 2
private const val DROP_LIBRARY_FILE_LINE_COUNT = 1

abstract class BaseDuplicateResourcesTask : DefaultTask(){

    @get:Input
    abstract val libraryResFiles: Property<FileCollection>

    @get:Input
    abstract val localResFiles: Property<FileCollection>

    @get:Input
    abstract val excludeResTypes: SetProperty<String>

    fun populateResourceStorage(resourcesNamesStorage: ResourcesNamesStorage) {
        val excludeTypes = excludeResTypes.get()
        libraryResFiles.get()
            // filter to exclude external libraries resource files. May be useful in future
            .filter { it.path.contains(BUILD_DIRECTORY) }
            .forEach { libraryResFile ->
                val project = findProjectByResFile(libraryResFile)
                libraryResFile
                    .readLines()
                    .drop(DROP_LIBRARY_FILE_LINE_COUNT)
                    .forEach { resLine ->
                        addNameToStorage(resourcesNamesStorage, resLine, excludeTypes, project)
                    }
            }

        // local file is only file in collection
        val localResFile = localResFiles.get().first()

        localResFile
            .readLines()
            .drop(DROP_LOCAL_FILE_LINE_COUNT)
            .forEach { resLine ->
                addNameToStorage(resourcesNamesStorage, resLine, excludeTypes, project)
            }
    }

    private fun addNameToStorage(
        storage: ResourcesNamesStorage,
        resourceLine: String,
        excludes: Set<String>,
        project: Project
    ) {
        val (type, resourceName) = resourceLine.split(" ")
        if(type !in excludes) {
            storage.add(type, resourceName, project.path)
        }
    }


    fun findResourceDuplicates(
        storage: ResourcesNamesStorage,
        baseline: ResourcesNamesStorage
    ): ResourcesNamesStorage {
        val result = ResourcesNamesStorage()
        storage.forEach { (type, resourcesInProjects) ->
            resourcesInProjects.forEach { (resName, projects) ->
                if (projects.size > 1) {
                    val baselineProjects = baseline.getProjects(type, resName)
                    if (projects.sorted() != baselineProjects.sorted()) {
                        result.addAll(type, resName, projects)
                    }
                }
            }
        }
        return result
    }
}