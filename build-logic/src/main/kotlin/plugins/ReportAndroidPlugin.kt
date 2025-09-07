package plugins

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.provider.SetProperty
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.create

class ReportAndroidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val android = project.extensions.getByType(AndroidComponentsExtension::class.java)

        val extension = project.extensions.create("report", ReportExtension::class.java)

        val configurations = mutableListOf<String>()

        var res: SetProperty<String>? = null


        android.onVariants { variant ->
            res = variant.packaging.resources.merges
            println(variant.name)
            configurations.add(variant.compileConfiguration.name)

            println()

            project.tasks.create<ReportTask>("${variant.name}GenerateReport") {

            }
        }


        project.afterEvaluate {
            println(configurations)
            project.configurations
                .matching {
                    it.name in configurations
                }
                .forEach {
                    println(it.name+": ")
                    val commponent = it.incoming.resolutionResult.rootComponent.get()
                    val deps = commponent.dependencies
                        .filter { it.requested is ProjectComponentSelector }
                        .map {
                            val requested = it.requested as ProjectComponentSelector
                            requested.projectPath
                        }
                    println(deps)

                }


            println()
            configurations.forEach {
                val deps = getProjectDependencies(it)
                println("$it: $deps")
            }
        }
    }
}