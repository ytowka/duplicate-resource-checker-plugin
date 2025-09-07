package plugins

import org.gradle.api.Project
import org.gradle.api.provider.Property

interface ReportExtension {

    //val rootProject: Property<Project>
    val configurations: Property<List<String>>
    //val subprojects: List<String>
}