plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("ru.alfabank.android.android-duplicate-res-checker")
    //id("ru.alfabank.android.android-duplicate-res-checker") version "1.0"
}

android {
    namespace = "com.example.androidtestapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.androidtestapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":feature-lib"))
}
