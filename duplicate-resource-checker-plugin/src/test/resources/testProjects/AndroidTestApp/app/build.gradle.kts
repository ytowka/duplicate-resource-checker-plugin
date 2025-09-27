plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("ru.alfabank.android.android-duplicate-res-checker")
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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

}