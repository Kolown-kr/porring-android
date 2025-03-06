import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.imageEdit")

dependencies {
    implementation(libs.androidx.foundation)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}