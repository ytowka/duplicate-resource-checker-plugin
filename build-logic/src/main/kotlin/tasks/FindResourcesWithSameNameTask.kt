package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class FindResourcesWithSameNameTask : DefaultTask() {

    @get:InputDirectory
    abstract val rootDirectory: DirectoryProperty

    private val logger = Logging.getLogger(this::class.java)

    @TaskAction
    fun act() {
        val resourcesNamesStorage = ResourcesNamesStorage()
        getRFiles().forEach {
            println("r file $it")
            addNamesFromRFileToStorage(it, resourcesNamesStorage)
        }
        val excludes = getExcludes()

        var wasFoundResourcesInMultipleModules = false
        resourcesNamesStorage.forEach { (type, resources) ->
            logger.quiet("type: $type")
            if (hasDuplicateNameResources(resources, excludes)) {
                wasFoundResourcesInMultipleModules = true
            }
        }
        if (wasFoundResourcesInMultipleModules) {
            throw Exception("wasFoundResourcesInMultipleModules")
        }
    }

    private fun getRFiles(): List<File> {
        val files = getModuleDirs().mapNotNull { moduleDir ->
            val symbolListDir = File("$moduleDir/build/intermediates/local_only_symbol_list/")
            val rFileDir = symbolListDir
                .listFiles()?.sortedBy { it.name }?.get(0)
                ?.listFiles()?.sortedBy { it.name }?.get(0)
            val rFile = File("$rFileDir/R-def.txt")

            if (rFile.exists()) {
                rFile
            } else {
                logger.quiet("File doesn't exist: $symbolListDir | $rFile")
                null
            }
        }
        return files
    }

    private fun hasDuplicateNameResources(
        resources: Map<String, List<File>>,
        excludes: List<String>,
    ): Boolean {
        var wasFoundResourcesInMultipleModules = false
        resources.forEach { (resourceName, files) ->
            if (files.size > 1 && resourceName !in excludes) {
                wasFoundResourcesInMultipleModules = true
                logger.quiet("     resourceName: $resourceName")
                printFilesModuleName(files)
            }
        }
        return wasFoundResourcesInMultipleModules
    }

    private fun printFilesModuleName(files: List<File>) {
        files.forEach { file ->
            val moduleName = file.path
                .substringAfter("${rootDirectory.asFile.get().path}/")
                .substringBefore("/build/")
            logger.quiet("         module: $moduleName")
        }
    }

    private fun getModuleDirs(): List<File> {
        val sourcesDirs = listOf(
            "${rootDirectory.asFile.get()}/feature-a",
            "${rootDirectory.asFile.get()}/feature-b",
        ).map { File(it) }
        return sourcesDirs
    }

    private fun addNamesFromRFileToStorage(
        rFile: File,
        storage: ResourcesNamesStorage,
    ) {
        rFile.readLines()
            .drop(2)
            .forEach {
                val (type, resourceName) = it.split(" ")
                if (type != "id") {
                    storage.add(type, resourceName, rFile)
                }
            }
    }

    private fun getExcludes(): List<String> {
        return emptyList()
        val excludesFile = File("${rootDirectory.asFile.get()}/config/resources_with_same_name_excludes.txt")
        return excludesFile.readLines()
    }
}