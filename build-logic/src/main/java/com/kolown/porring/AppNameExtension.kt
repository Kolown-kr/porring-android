package com.kolown.porring

import org.gradle.api.Project

fun Project.setNamespace(name: String) {
    androidExtension.apply {
        namespace = "com.kolown.porring.$name"
    }
}