package com.kolown.porring

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureComposeAndroid() {
    with(plugins) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }

    androidExtension.apply {
        dependencies {
            val bom = libs.androidx.compose.bom

            implementations(
                platform(bom),
                libs.androidx.compose.material3,
                libs.androidx.compose.ui,
                libs.androidx.compose.ui.tooling.preview,
                libs.androidx.activity.compose,
                libs.androidx.navigation.compose,
                libs.androidx.hilt.navigation.compose
            )

            androidTestImplementations(
                platform(bom),
                libs.androidx.compose.ui.test.junit4
            )

            debugImplementations(
                libs.androidx.compose.ui.tooling,
                libs.androidx.compose.ui.test.manifest
            )
        }
    }

    extensions.getByType<ComposeCompilerGradlePluginExtension>().apply {
        enableStrongSkippingMode.set(true)
        includeSourceInformation.set(true)
    }
}