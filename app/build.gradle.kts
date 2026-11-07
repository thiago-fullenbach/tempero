import java.util.Properties

plugins {
    alias(libs.plugins.tempero.android.application)
    alias(libs.plugins.tempero.android.compose)
    alias(libs.plugins.tempero.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Lê o local.properties via provider para o configuration cache rastrear o arquivo como entrada.
fun localProperty(name: String): String? =
    providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText
        .map { text -> Properties().apply { load(text.reader()) }.getProperty(name) }
        .orNull

android {
    namespace = "br.com.thiago.tempero"

    defaultConfig {
        applicationId = "br.com.thiago.tempero"
        // No CI de release, a versão é derivada da tag (-Ptempero.versionName / -Ptempero.versionCode).
        versionCode = (findProperty("tempero.versionCode") as String?)?.toInt() ?: 1
        versionName = (findProperty("tempero.versionName") as String?) ?: "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // `ANALYTICS_IN_DEBUG=true` no local.properties liga o Analytics em builds de debug.
        buildConfigField(
            "boolean", "ANALYTICS_IN_DEBUG",
            (localProperty("ANALYTICS_IN_DEBUG") ?: "false").toBoolean().toString()
        )
    }

    // Assinatura de release: só é configurada quando o keystore é informado via variáveis de ambiente (CI).
    val releaseKeystorePath = System.getenv("TEMPERO_KEYSTORE_PATH")
    signingConfigs {
        if (releaseKeystorePath != null) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = System.getenv("TEMPERO_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("TEMPERO_KEY_ALIAS")
                keyPassword = System.getenv("TEMPERO_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (releaseKeystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:analytics"))
    testImplementation(project(":core:testing"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.timber)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.coil.test)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    kspTest(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
