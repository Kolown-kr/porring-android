import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

android {
    setNamespace("feature.camera")
}

dependencies {
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
    implementation(libs.exifinterface)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)

    //camera(with optional)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extension)
    implementation(libs.kotlinx.coroutines.guava)

    // Jetpack Compose nav
//    implementation(libs.androidx.navigation.compose)
}
