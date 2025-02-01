import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
    alias(libs.plugins.kotlin.serialization)
}

setNamespace("core.model")

dependencies {
    implementation(libs.kotlinx.serialization.json)
}