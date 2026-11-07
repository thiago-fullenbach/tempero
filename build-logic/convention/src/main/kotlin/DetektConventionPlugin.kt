import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                config.setFrom(rootProject.file("config/detekt/detekt.yml"))
                baseline = file("detekt-baseline.xml")
                autoCorrect = false
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    sarif.required.set(true)
                    xml.required.set(false)
                    txt.required.set(false)
                    md.required.set(false)
                }
            }

            dependencies {
                add("detektPlugins", libs.lib("detekt-formatting"))
            }
        }
    }
}
