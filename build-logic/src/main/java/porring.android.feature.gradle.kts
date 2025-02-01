import com.kolown.porring.configureCoroutineAndroid

plugins {
    id("porring.android.library")
    id("porring.android.compose")
}

configureCoroutineAndroid()

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
}