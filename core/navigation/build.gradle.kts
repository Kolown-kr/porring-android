plugins {
    alias(libs.plugins.porring.kotlin.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
}