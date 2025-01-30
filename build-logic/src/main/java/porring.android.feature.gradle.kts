import com.kolown.porring.configureCoroutineAndroid
import com.kolown.porring.configureHiltAndroid

plugins {
    id("porring.android.library")
    id("porring.android.hilt")
}

configureHiltAndroid()
configureCoroutineAndroid()

dependencies {
    add("implementation", project(":core:common"))
    add("implementation", project(":core:data"))
    add("implementation", project(":core:domain"))
    add("implementation", project(":core:designsystem"))
    add("implementation", project(":core:navigation"))
    add("implementation", project(":core:ui"))
}