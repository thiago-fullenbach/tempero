plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.hilt)
}

android {
    namespace = "br.com.thiago.tempero.core.analytics"
}

dependencies {
    api(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.timber)
}
