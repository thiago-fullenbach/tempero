import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/** Módulo Kotlin/JVM puro (sem Android), para `core:model` e `core:domain`. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply(DetektConventionPlugin::class.java)

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
            configureKotlinJvmTarget()

            // Mantém `./gradlew testDebugUnitTest` (usado no CI) executando também estes módulos.
            tasks.register("testDebugUnitTest") {
                group = "verification"
                description = "Executa os testes unitarios (alias de test)."
                dependsOn("test")
            }

            dependencies {
                add("testImplementation", libs.lib("junit"))
                add("testImplementation", libs.lib("kotlinx-coroutines-test"))
                add("testImplementation", libs.lib("turbine"))
            }
        }
    }
}
