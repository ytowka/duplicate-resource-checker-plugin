package ru.alfabank.android.duplicateres.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import ru.alfabank.android.duplicateres.internal.ResourcesNamesStorage
import ru.alfabank.android.duplicateres.internal.findDuplicates
import java.io.File
import java.io.PrintWriter

public abstract class CreateBaselineDuplicateResourcesTask : BaseDuplicateResourcesTask() {

    @get:OutputFile
    public abstract val baselineFile: RegularFileProperty

    @TaskAction
    public fun act() {
        val resourcesNamesStorage = ResourcesNamesStorage()
        populateResourceStorage(resourcesNamesStorage)

        val duplicates = resourcesNamesStorage.findDuplicates(ResourcesNamesStorage())
        baselineFile.get().asFile.apply {
            createNewFile()
            writeBaseline(duplicates)
        }
    }

    private fun File.writeBaseline(duplicates: ResourcesNamesStorage) {
        PrintWriter(this).use { writer ->
            duplicates.forEach { (type, resourcesInProjects) ->
                resourcesInProjects.forEach { (resName, projects) ->
                    writer.println(
                        buildString {
                            append(type)
                            append(" ")
                            append(resName)
                            append(" ")
                            append(
                                projects
                                    .sorted()
                                    .joinToString(separator = " ")
                            )
                        }
                    )
                }
            }
        }
    }
}
