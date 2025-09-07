import tasks.GenerateReportTask
import tasks.ReportPluginExtension

val reportGeneratorConfig = extensions.create<ReportPluginExtension>("report")

tasks.register<GenerateReportTask>("generateReport") {
    sourceDirectory.convention(reportGeneratorConfig.sourceDirectory)
    reportFile.set(layout.buildDirectory.file("fileReport.txt"))
}