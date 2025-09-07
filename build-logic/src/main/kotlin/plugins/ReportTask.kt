package plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ReportTask : DefaultTask() {

    /*@get:InputDirectory
    abstract val project: RegularFileProperty
*/

    @TaskAction
    fun act() {

    }
}
