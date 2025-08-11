plugins {
    alias(libs.plugins.google)
    alias(libs.plugins.porring.android.application)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.kolown.porring"

    defaultConfig {
        applicationId = "com.kolown.porring"
        versionCode = 7
        versionName = "1.1.0-rc.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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