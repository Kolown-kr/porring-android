import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

android {
    setNamespace("feature.home")
}

dependencies {
    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //lottie
    implementation(libs.lottie.compose)
}