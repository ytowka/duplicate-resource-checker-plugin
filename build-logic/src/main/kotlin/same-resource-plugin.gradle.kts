import tasks.FindResourcesWithSameNameTask

tasks.register<FindResourcesWithSameNameTask>("findResourcesWithSameName") {

    for(project in subprojects) {
        val resTask = project.tasks.matching {
            val matches = it.name.contains("LocalResources")
            matches
        } ?: continue
        //println("project parse: ${project.name}, resTask: ${resTask.names}")
        dependsOn(resTask)
    }
    rootDirectory = rootProject.rootDir
}