import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.main")

dependencies {
    implementation(libs.kotlinx.immutable)

    implementation(projects.feature.camera)
    implementation(projects.feature.detail)
    implementation(projects.feature.home)
    implementation(projects.feature.follower)
    implementation(projects.feature.join)
    implementation(projects.feature.login)
    implementation(projects.feature.my)
    implementation(projects.feature.search)
    implementation(projects.feature.setting)
    implementation(projects.feature.their)
    implementation(projects.feature.upload)
}