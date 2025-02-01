import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

setNamespace("feature.upload")

dependencies {
    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.kotlinx.serialization.json)
}