plugins {
    alias(libs.plugins.tempero.jvm.library)
    `java-test-fixtures`
}

dependencies {
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
    api(libs.jakarta.inject.api)

    // Fakes e utilitários de teste do domínio; reexportados por :core:testing.
    testFixturesApi(libs.junit)
    testFixturesApi(libs.kotlinx.coroutines.test)
}
