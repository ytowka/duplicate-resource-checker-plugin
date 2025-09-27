package ru.alfabank.android.duplicateres.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import ru.alfabank.android.duplicateres.internal.ResourceInMultipleModulesException
import ru.alfabank.android.duplicateres.internal.ResourcesNamesStorage
import ru.alfabank.android.duplicateres.internal.findDuplicates

public abstract class FindDuplicateResourcesTask : BaseDuplicateResourcesTask() {

    @get:InputFile
    @get:Optional
    public abstract val baselineFile: RegularFileProperty

    private val logger = Logging.getLogger(this::class.java)

    @TaskAction
    public fun act() {
        val resourcesNamesStorage = ResourcesNamesStorage()
        populateResourceStorage(resourcesNamesStorage)
        val baseline = loadBaseline()
        val duplicates = resourcesNamesStorage.findDuplicates(baseline)
        if (!duplicates.isEmpty) {
            logDuplicates(duplicates)
            throw ResourceInMultipleModulesException()
        }
    }

    private fun loadBaseline(): ResourcesNamesStorage {
        val baselineStorage = ResourcesNamesStorage()

        if (baselineFile.isPresent) {
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
                logger.error(
                    buildString {
                        appendLine("    \"$resName\" is present in multiple projects:")
                        projects.forEach {
                            appendLine("        $it")
                        }
                    }
                )
            }
        }
    }
}
