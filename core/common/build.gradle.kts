import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
    id("porring.android.compose")
}

android {
    setNamespace("core.common")
}

dependencies {
    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.paging.compose)

    implementation(projects.core.designsystem)
    implementation(projects.core.model)
}
