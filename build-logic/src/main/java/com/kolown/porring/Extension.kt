package com.kolown.porring

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import gradle.kotlin.dsl.accessors._2fb5859a04200edaf14b854c40b2e363.androidTestImplementation
import gradle.kotlin.dsl.accessors._2fb5859a04200edaf14b854c40b2e363.debugImplementation
import gradle.kotlin.dsl.accessors._2fb5859a04200edaf14b854c40b2e363.implementation
import gradle.kotlin.dsl.accessors._2fb5859a04200edaf14b854c40b2e363.testImplementation
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

internal val Project.applicationExtension: CommonExtension<*, *, *, *, *, *>
    get() = extensions.getByType<ApplicationExtension>()

internal val Project.libraryExtension: CommonExtension<*, *, *, *, *, *>
    get() = extensions.getByType<LibraryExtension>()

internal val Project.androidExtension: CommonExtension<*, *, *, *, *, *>
    get() = kotlin.runCatching { libraryExtension }
        .recoverCatching { applicationExtension }
        .onFailure { println("Could not find Library or Application extension from this project") }
        .getOrThrow()

internal val Project.libs get() = the<LibrariesForLibs>()

internal fun DependencyHandlerScope.implementations(vararg notations: Any) {
    notations.forEach { notation ->
        implementation(notation)
    }
}

internal fun DependencyHandlerScope.debugImplementations(vararg notations: Any) {
    notations.forEach { notation ->
        debugImplementation(notation)
    }
}

internal fun DependencyHandlerScope.testImplementations(vararg notations: Any) {
    notations.forEach { notation ->
        testImplementation(notation)
    }
}

internal fun DependencyHandlerScope.androidTestImplementations(vararg notations: Any) {
    notations.forEach { notation ->
        androidTestImplementation(notation)
    }
}

internal fun DependencyHandlerScope.ksp(vararg notations: Any) {
    notations.forEach { notation ->
        add("ksp", notation)
    }
}

internal fun DependencyHandlerScope.kspAndroidTest(vararg notations: Any) {
    notations.forEach { notation ->
        add("kspAndroidTest", notation)
    }
}