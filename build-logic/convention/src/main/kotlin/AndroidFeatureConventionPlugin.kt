import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Módulo de funcionalidade (tela): biblioteca Android com Compose e Hilt que só enxerga
 * `core:ui`, `core:designsystem`, `core:domain` e `core:model` — nunca data/network/database
 * nem outras features.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(AndroidLibraryConventionPlugin::class.java)
            pluginManager.apply(AndroidComposeConventionPlugin::class.java)
            pluginManager.apply(HiltConventionPlugin::class.java)
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:model"))

                add("implementation", libs.lib("androidx-lifecycle-runtime-ktx"))
                add("implementation", libs.lib("androidx-hilt-navigation-compose"))
                add("implementation", libs.lib("androidx-navigation-compose"))
                add("implementation", libs.lib("kotlinx-serialization-core"))
                add("implementation", libs.lib("coil-compose"))
                add("implementation", libs.lib("timber"))

                add("testImplementation", project(":core:testing"))
                add("testImplementation", libs.lib("turbine"))
                add("testImplementation", libs.lib("robolectric"))
                add("testImplementation", libs.lib("androidx-test-core-ktx"))
                add("testImplementation", libs.lib("androidx-compose-ui-test-junit4"))
            }
        }
    }
}
