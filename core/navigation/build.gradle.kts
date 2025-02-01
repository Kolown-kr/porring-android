import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.porring.android.library)
}

setNamespace("core.navigation")

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
}