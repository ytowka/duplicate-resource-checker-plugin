plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    // https://mvnrepository.com/artifact/com.android.tools.build/gradle
    implementation("com.android.tools.build:gradle:8.9.1")
}

gradlePlugin {
    plugins {
        create("reportPlugin") {
            id = "org.danilkha.report"
            implementationClass = "plugins.ReportPlugin"
        }
        create("reportAndroidPlugin") {
            id = "org.danilkha.reportAndroid"
            implementationClass = "plugins.ReportAndroidPlugin"
        }
    }
}