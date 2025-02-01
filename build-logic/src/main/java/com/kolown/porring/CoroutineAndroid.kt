package com.kolown.porring

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCoroutineAndroid() {
    configureCoroutineKotlin()
    dependencies {
        implementations(
            libs.coroutines.android
        )
    }
}

internal fun Project.configureCoroutineKotlin() {
    dependencies {
        implementations(
            libs.coroutines.core
        )
        testImplementations(
            libs.coroutines.test
        )
    }
}