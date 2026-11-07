plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.android.compose)
}

android {
    namespace = "br.com.thiago.tempero.core.ui"
}

dependencies {
    api(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(libs.coil.compose)
}
