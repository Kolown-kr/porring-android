import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

setNamespace("feature.join")

dependencies {
    implementation(libs.google.firebase.auth)
}
