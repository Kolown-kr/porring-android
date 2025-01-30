import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

android {
    setNamespace("feature.my")
}

dependencies {
    //paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
