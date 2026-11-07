import java.util.Properties

plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.hilt)
}

// Lê o local.properties via provider para o configuration cache rastrear o arquivo como entrada.
fun localProperty(name: String): String? =
    providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText
        .map { text -> Properties().apply { load(text.reader()) }.getProperty(name) }
        .orNull

android {
    namespace = "br.com.thiago.tempero.core.network"

    defaultConfig {
        // Chave da TheMealDB: `MEALDB_API_KEY` no local.properties ou no ambiente; padrão "1" (chave pública de testes).
        buildConfigField(
            "String", "MEALDB_API_KEY",
            "\"${localProperty("MEALDB_API_KEY") ?: System.getenv("MEALDB_API_KEY") ?: "1"}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Exceções de HTTP/JSON fazem parte do contrato visto por quem consome o data source.
    api(libs.retrofit)
    api(libs.moshi)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.timber)
    ksp(libs.moshi.kotlin.codegen)
}
