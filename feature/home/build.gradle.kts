import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.home")

dependencies {
    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //lottie
    implementation(libs.lottie.compose)
}