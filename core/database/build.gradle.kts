plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.hilt)
    alias(libs.plugins.tempero.android.room)
}

android {
    namespace = "br.com.thiago.tempero.core.database"
}

dependencies {
    // `TemperoDatabase` estende `RoomDatabase`, então o Room faz parte da API do módulo.
    api(libs.androidx.room.runtime)
    implementation(project(":core:model"))
    implementation(libs.moshi)
    implementation(libs.timber)
    ksp(libs.moshi.kotlin.codegen)
}
