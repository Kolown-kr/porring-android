import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.detail")

dependencies {
    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //paging3
    implementation(libs.androidx.paging.compose)

    implementation(libs.kotlinx.serialization.json)
}