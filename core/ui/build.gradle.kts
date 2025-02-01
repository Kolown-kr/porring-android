import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
    id("porring.android.compose")
}

setNamespace("core.ui")

dependencies {
    implementation(projects.core.designsystem)
}