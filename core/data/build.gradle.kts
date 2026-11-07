plugins {
    alias(libs.plugins.tempero.android.library)
    alias(libs.plugins.tempero.hilt)
}

android {
    namespace = "br.com.thiago.tempero.core.data"
}

dependencies {
    api(project(":core:domain"))
    // Expostos com `api` para o Hilt do :app enxergar os módulos de DI de common/network/database.
    api(project(":core:common"))
    api(project(":core:network"))
    api(project(":core:database"))
    implementation(project(":core:model"))
    implementation(libs.timber)

    testImplementation(project(":core:testing"))
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.turbine)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.retrofit.converter.moshi)
}
