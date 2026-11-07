plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.android.compose)
}

android {
    namespace = "br.com.thiago.tempero.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.ui.text.google.fonts)
}
