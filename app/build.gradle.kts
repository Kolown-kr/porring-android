plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.google)
    alias(libs.plugins.kotlin.compose)
    id("porring.android.application")
}

android {
    namespace = "com.kolown.porring"

    defaultConfig {
        applicationId = "com.kolown.porring"
        versionCode = 5
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.runtime)
    implementation(projects.feature.main)
}