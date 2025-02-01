package com.kolown.porring

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureJUnitAndroid() {
    dependencies {
        implementations(
            libs.junit
        )
        androidTestImplementations(
            libs.androidx.junit,
            libs.androidx.junit.ktx
        )
    }
}