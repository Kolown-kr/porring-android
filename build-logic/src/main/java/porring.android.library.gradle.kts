import com.kolown.porring.configureHiltAndroid
import com.kolown.porring.configureJUnitAndroid
import com.kolown.porring.configureKotlinAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureHiltAndroid()
configureJUnitAndroid()

dependencies {
    testImplementation(project(":core:testing"))
}