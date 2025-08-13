import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.my")

dependencies {
    //paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
