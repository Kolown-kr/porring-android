import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
}

setNamespace("core.data.api")

dependencies {
    //paging
    implementation(libs.androidx.paging.runtime)
    implementation(project(":core:model"))
}
