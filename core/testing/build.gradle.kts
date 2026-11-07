plugins {
    alias(libs.plugins.tempero.android.library)
}

android {
    namespace = "br.com.thiago.tempero.core.testing"
}

dependencies {
    // Fakes e utilitários do domínio (meal(), FakeMealRepository, MainDispatcherRule...) + JUnit/coroutines-test.
    api(testFixtures(project(":core:domain")))
    api(project(":core:domain"))
    api(project(":core:network"))
    api(project(":core:database"))
}
