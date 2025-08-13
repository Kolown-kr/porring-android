plugins {
    alias(libs.plugins.google)
    alias(libs.plugins.porring.android.application)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.kolown.porring"

    defaultConfig {
        applicationId = "com.kolown.porring"
        versionCode = 6
        versionName = "1.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.core.local)
    implementation(projects.core.datastore)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}