import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
    alias(libs.plugins.ksp)
}

setNamespace("core.local")

dependencies {
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)
}