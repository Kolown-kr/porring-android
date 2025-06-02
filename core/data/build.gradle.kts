import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
}

setNamespace("core.data")

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.common)

    implementation(libs.exifinterface)
    implementation(libs.androidx.credentials)
    implementation(libs.google.android.googleid)
    implementation(libs.google.firebase.firestore)

    //paging
    implementation(libs.androidx.paging.runtime)

}

