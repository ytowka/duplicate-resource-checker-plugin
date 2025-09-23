import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.lint)
}

group = "com.alfabank.duplicateres"
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

    testImplementation(kotlin("test-junit5"))

    detektPlugins(libs.detekt.formatting)
    lintChecks(libs.androidx.lint)
}

gradlePlugin {
    plugins {
        create("duplicateResPlugin") {
            id = "com.alfabank.duplicate-res"
            implementationClass = "com.alfabank.duplicateres.plugin.DuplicateResourceCheckerPlugin"
            displayName = "Duplicate android resources checker plugin"
            description = "A plugin for checking duplicate android resources across all modules"
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<Detekt>("detekt") {
    config = project.files("../config/detekt/detekt-environment.yml")
}
