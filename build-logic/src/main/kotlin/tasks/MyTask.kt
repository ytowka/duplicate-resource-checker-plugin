package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class MyTask : DefaultTask() {

    @get:Input
    abstract val text: Property<String>

    @get:OutputFile
    abstract val outFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val textString = text.get()
        val file = outFile.get().asFile

        file.writeText(textString)
    }
}