import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
}

group = "ru.alfabank.android.androidduplicatereschecker"
version = "1.0"

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
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.alfabank.android.android-duplicate-res-checker"
            artifactId = "ru.alfabank.android.android-duplicate-res-checker"
            version = "1.0"

            from(components.named("java").get())
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
