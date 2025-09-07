

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    //id("org.danilkha.reportAndroid")
    id("same-resource-plugin")
}


/*report {
    sourceDirectory = layout.projectDirectory.dir("feature-a")
}*/

tasks.register("hello") {
    doLast {
        println("Hello")
    }
}

tasks.register("world") {
    doLast {
        println("world")
    }
    dependsOn("hello")
}

