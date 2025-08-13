import com.kolown.porring.setNamespace
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.porring.android.feature)
}

setNamespace("feature.login")

var properties = Properties()
properties.load(FileInputStream("local.properties"))


android {
    defaultConfig {
        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            properties.getProperty("google_cient_id")
        )
    }
}

dependencies {
    // credential, auth
    implementation(libs.androidx.credentials)
    implementation(libs.google.android.googleid)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.androidx.credentials.play.services.auth)
}
