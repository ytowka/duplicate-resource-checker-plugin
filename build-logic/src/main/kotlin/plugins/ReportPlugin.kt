package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import tasks.GenerateReportTask
import tasks.ReportPluginExtension

class ReportPlugin : Plugin<Project>{

    override fun apply(target: Project) {
        val reportGeneratorConfig = target.extensions.create<ReportPluginExtension>("report")

        target.tasks.register<GenerateReportTask>("generateReport") {
            sourceDirectory.convention(reportGeneratorConfig.sourceDirectory)
            reportFile.set(target.layout.buildDirectory.file("fileReport.txt"))
        }
    }
}