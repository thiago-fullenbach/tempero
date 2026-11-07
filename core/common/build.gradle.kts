plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.hilt)
}

android {
    namespace = "br.com.thiago.tempero.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
