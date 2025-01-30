import com.kolown.porring.setNamespace
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id("porring.android.feature")
}

var properties = Properties()
properties.load(FileInputStream("local.properties"))


android {
    setNamespace("feature.login")

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_CLIENT_ID",
                properties.getProperty("google_cient_id")
            )
        }
    }
}

dependencies {
    // credential, auth
    implementation(libs.androidx.credentials)
    implementation(libs.google.android.googleid)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.androidx.credentials.play.services.auth)
}
