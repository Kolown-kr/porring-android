import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
}

setNamespace("core.data")

dependencies {
    implementation(libs.exifinterface)
    implementation(libs.androidx.credentials)
    implementation(libs.google.android.googleid)

    //paging
    implementation(libs.androidx.paging.runtime)

    api(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
}

