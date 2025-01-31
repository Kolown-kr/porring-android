plugins {
    alias(libs.plugins.kotlin.serialization)
    id("porring.kotlin.library")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}