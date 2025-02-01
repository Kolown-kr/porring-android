import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.their")

dependencies {
    //paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}