import com.kolown.porring.setNamespace

plugins {
    alias(libs.plugins.porring.android.library)
}

setNamespace("core.data.api")

dependencies {
    implementation(projects.core.model)
    
    //paging
    implementation(libs.androidx.paging.runtime)

}
