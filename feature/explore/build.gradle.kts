plugins {
    alias(libs.plugins.tempero.android.feature)
}

android {
    namespace = "br.com.thiago.tempero.feature.explore"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.androidx.paging.testing)
}
