import com.android.build.api.variant.AndroidComponentsExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //id("org.danilkha.reportAndroid")
    id("org.danilkha.duplicateRes")
}

android {

    namespace = "com.danilkha.duplicateresourcechecker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.danilkha.duplicateresourcechecker"
        minSdk = 26
        targetSdk = 36
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
    flavorDimensions += "server"
    flavorDimensions += "store"
    productFlavors {
        create("prod") {
            dimension = "server"
        }
        create("dev") {
            dimension = "server"
        }
        create("google") {
            dimension = "store"
        }
        create("huawei") {
            dimension = "store"
        }
        create("rustore") {
            matchingFallbacks += listOf("google")
            dimension = "store"
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

    implementation(project(":feature-a"))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":feature-b"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

duplicateResourceFinder {
    excludeResourceType = setOf("id")
}