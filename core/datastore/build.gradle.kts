import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
}

setNamespace("core.datastore")

dependencies {
    //datastore
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    implementation(projects.core.model)
}
