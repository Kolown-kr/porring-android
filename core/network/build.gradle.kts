import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
    alias(libs.plugins.kotlin.serialization)
}

setNamespace("core.network")

android {
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.storage)
    implementation(libs.google.firebase.auth)

    implementation(libs.firebase.config)

    // credential, auth
    implementation(libs.androidx.credentials)
    implementation(libs.google.play.services.auth)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.android.googleid)

    implementation(projects.core.model)

    //retrofit
    implementation(platform(libs.retrofit.bom))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.retrofitBundle)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.androidx.arch.core.testing)

    testImplementation(libs.coroutines.test)

    implementation(libs.kotlinx.serialization.json)
}
