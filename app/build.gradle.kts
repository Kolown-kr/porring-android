import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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

    signingConfigs {
        create("release") {
            storeFile = file(getProperty("KEYSTORE_FILE"))
            storePassword = getProperty("KEYSTORE_PASSWORD")
            keyAlias = getProperty("KEY_ALIAS")
            keyPassword = getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
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

fun getProperty(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key)
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.core.local)
    implementation(projects.core.datastore)
    implementation(projects.core.network)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}