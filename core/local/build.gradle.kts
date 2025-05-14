import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
    alias(libs.plugins.ksp)
}

setNamespace("core.local")

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.dataApi)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.runtime.ktx)
}