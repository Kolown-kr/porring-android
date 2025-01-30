import com.kolown.porring.setNamespace

plugins {
    id("porring.android.feature")
}

android {
    setNamespace("feature.join")
}

dependencies {
    implementation(libs.google.firebase.auth)
}
