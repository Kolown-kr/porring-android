import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
    alias(libs.plugins.porring.android.compose)
}

setNamespace("core.ui")

dependencies {
    implementation(projects.core.designsystem)
}