import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
}

group = "ru.alfabank.android.androidduplicatereschecker"
version = "0.1"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
    explicitApi()
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

gradlePlugin {
    plugins {
        create("duplicateResPlugin") {
            id = "ru.alfabank.android.android-duplicate-res-checker"
            implementationClass = "ru.alfabank.android.duplicateres.plugin.DuplicateResourceCheckerPlugin"
            displayName = "Duplicate android resources checker plugin"
            description = "A plugin for checking duplicate android resources across all modules"
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
