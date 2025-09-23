package duplicateres.tasks

import duplicateres.plugin.ResourceType
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

abstract class BaseDuplicateResourcesTask : DefaultTask(){

    @get:InputFiles
    abstract val libraryResFiles: ConfigurableFileCollection

    @get:InputFiles
    abstract val localResFiles: ConfigurableFileCollection

    @get:Input
    abstract val excludeResTypes: SetProperty<ResourceType>

    @get:Input
    abstract val projectPaths: MapProperty<String, String>

    @get:Input
    abstract val appProjectPath: Property<String>

    fun populateResourceStorage(resourcesNamesStorage: ResourcesNamesStorage) {
        val excludeTypes = excludeResTypes.get()
            .map { it.type }
            .toSet()

        libraryResFiles
            // filter to exclude external libraries resource files. May be useful in future
            .filter {
                println(it.path)
                it.path.contains(BUILD_DIRECTORY)
            }
            .forEach { libraryResFile ->
                val projectDir = libraryResFile.path.substringBefore(BUILD_DIRECTORY)
                val projectPath = projectPaths.get()[projectDir] ?: ""
                libraryResFile
                    .readLines()
                    .drop(DROP_LIBRARY_FILE_LINE_COUNT)
                    .forEach { resLine ->
                        addNameToStorage(resourcesNamesStorage, resLine, excludeTypes, projectPath)
                    }
            }

        // local file is only file in collection
        val localResFile = localResFiles.first()

        localResFile
            .readLines()
            .drop(DROP_LOCAL_FILE_LINE_COUNT)
            .forEach { resLine ->
                addNameToStorage(resourcesNamesStorage, resLine, excludeTypes, appProjectPath.get())
            }
    }

    private fun addNameToStorage(
        storage: ResourcesNamesStorage,
        resourceLine: String,
        excludes: Set<String>,
        project: String
    ) {
        val (type, resourceName) = resourceLine.split(" ")
        if(type !in excludes) {
            storage.add(type, resourceName, project)
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