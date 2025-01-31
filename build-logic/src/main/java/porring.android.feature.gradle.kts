import com.kolown.porring.configureCoroutineAndroid

plugins {
    id("porring.android.library")
    id("porring.android.compose")
}

configureCoroutineAndroid()

dependencies {
    add("implementation", project(":core:common"))
    add("implementation", project(":core:data"))
//    add("implementation", project(":core:domain"))
    add("implementation", project(":core:designsystem"))
    add("implementation", project(":core:navigation"))
    add("implementation", project(":core:ui"))
}