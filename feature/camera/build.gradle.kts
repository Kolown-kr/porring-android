import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.camera")

dependencies {
    implementation(libs.exifinterface)

    //camera(with optional)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extension)
    implementation(libs.kotlinx.coroutines.guava)
}
