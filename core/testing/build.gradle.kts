import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
}

setNamespace("core.testing")

dependencies {
    api(libs.mockk)
    api(libs.coroutines.test)
    api(libs.mockk.android)
    api(libs.turbine)
    api(libs.junit)
    api(libs.robolectric)
}