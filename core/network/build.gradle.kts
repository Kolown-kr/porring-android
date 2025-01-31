import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
}

android {
    setNamespace("core.network")
}

dependencies {
    // firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.storage)
    implementation(libs.google.firebase.auth)

    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // credential, auth
    implementation(libs.androidx.credentials)
    implementation(libs.google.play.services.auth)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.android.googleid)

    implementation(projects.core.model)
}
