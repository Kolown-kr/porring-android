import com.kolown.porring.setNamespace

plugins {
    id("porring.android.library")
}

android {
    setNamespace("core.datastore")
}

dependencies {
    //datastore
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    implementation(projects.core.model)
}
