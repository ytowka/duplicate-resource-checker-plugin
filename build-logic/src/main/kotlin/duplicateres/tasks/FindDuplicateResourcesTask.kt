package duplicateres.tasks


import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction


abstract class FindDuplicateResourcesTask : BaseDuplicateResourcesTask(){

    @get:InputFile
    @get:Optional
    abstract val baselineFile: RegularFileProperty

    private val logger = Logging.getLogger(this::class.java)

    @TaskAction
    fun act() {
        val resourcesNamesStorage = ResourcesNamesStorage()
        populateResourceStorage(resourcesNamesStorage)
        val baseline = loadBaseline()
        val duplicates = findResourceDuplicates(resourcesNamesStorage, baseline)
        if (!duplicates.isEmpty) {
            logDuplicates(duplicates)
            throw Exception("wasFoundResourcesInMultipleModules")
        }
    }

    private fun loadBaseline(): ResourcesNamesStorage {
        val baselineStorage = ResourcesNamesStorage()

        if(baselineFile.isPresent) {
            baselineFile.get().asFile.forEachLine {
                val splitLine = it.split(" ")
                val (type, name) = splitLine
                val projects = splitLine.drop(2)
                baselineStorage.addAll(type, name, projects)
            }
        }

        return baselineStorage
    }

    private fun logDuplicates(duplicates: ResourcesNamesStorage) {
        duplicates.forEach { (type, resourcesInProjects) ->
            logger.error("type: $type")
            resourcesInProjects.forEach { (resName, projects) ->
                logger.error(buildString {
                    appendLine("    \"$resName\" is present in multiple projects:")
                    projects.forEach {
                        appendLine("        $it")
                    }
                })
            }
        }
    }
}