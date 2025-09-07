import tasks.FindResourcesWithSameNameTask

tasks.register<FindResourcesWithSameNameTask>("findResourcesWithSameName") {

    for(project in subprojects) {
        val resTask = project.tasks.findByName("parseDebugLocalResources") ?: continue
        dependsOn(resTask)
    }
    rootDirectory = rootProject.rootDir
}