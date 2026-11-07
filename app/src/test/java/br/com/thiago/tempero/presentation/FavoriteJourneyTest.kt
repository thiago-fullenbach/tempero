package br.com.thiago.tempero.presentation

import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import br.com.thiago.tempero.core.testing.fake.FakeMealRemoteDataSource
import br.com.thiago.tempero.core.testing.util.categoryDto
import br.com.thiago.tempero.core.testing.util.mealDto
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jakarta.inject.Inject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Único teste de UI do app. Percorre a jornada completa com Hilt real (só as bordas são falsas):
 * Explorar → abre a receita → favorita → volta → Favoritos mostra a receita → abre →
 * desfavorita → volta (Favoritos vazio) → aba Explorar.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoilApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, qualifiers = "w411dp-h891dp-xxhdpi")
class FavoriteJourneyTest {
    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    // A Activity só sobe depois de configurar a API falsa; com createAndroidComposeRule ela
    // subiria antes do @Before e o Explorar carregaria uma lista vazia.
    @get:Rule(order = 1)
    val compose = createEmptyComposeRule()

    @Inject
    lateinit var remote: FakeMealRemoteDataSource

    private var scenario: ActivityScenario<MainActivity>? = null

    private val recipe = "Beef and Mustard Pie"

    @Before
    fun setUp() {
        hilt.inject()

        // Sem rede para as imagens.
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val engine = FakeImageLoaderEngine.Builder().default(Color.GRAY.toDrawable()).build()
        Coil.setImageLoader(ImageLoader.Builder(context).components { add(engine) }.build())

        remote.categories = listOf(categoryDto("Beef"))
        // filter.php: item sem ingredientes nem preparo, sem strCategory.
        remote.byCategory["Beef"] = listOf(mealDto(id = "52874", name = recipe))
        // lookup.php: a receita completa.
        remote.byId[52874] = mealDto(
            id = "52874",
            name = recipe,
            category = "Beef",
            instructions = "Preheat the oven. Mix well.",
            ingredients = listOf("Beef", "Mustard"),
            measures = listOf("1kg", "2 tbs")
        )

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario?.close()
    }

    private fun textExists(text: String) =
        compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun favoritarVerEmFavoritosDesfavoritarEVoltarParaExplorar() {
        // 1. Explorar: espera a lista carregar e abre a receita.
        compose.waitUntil(TIMEOUT_MS) { textExists(recipe) }
        compose.onNodeWithText(recipe).performClick()

        // 2. Detalhe: favorita e espera o coração virar "remover".
        compose.waitUntil(TIMEOUT_MS) { contentDescriptionExists("Add to favorites") }
        compose.onNodeWithContentDescription("Add to favorites").performClick()
        compose.waitUntil(TIMEOUT_MS) { contentDescriptionExists("Remove from favorites") }

        // 3. Volta para o Explorar.
        compose.onNodeWithContentDescription("Back").performClick()
        compose.waitUntil(TIMEOUT_MS) { textExists("What are we cooking today?") && !contentDescriptionExists("Back") }

        // 4. Aba Favoritos: a mesma receita aparece na lista.
        compose.onNodeWithText("Favorites").performClick()
        compose.waitUntil(TIMEOUT_MS) { contentDescriptionExists("Remove from favorites") }
        compose.onNodeWithText(recipe).assertIsDisplayed()

        // 5. Abre a receita e desfavorita. Espera o Detalhe ocupar a tela (a lista de Favoritos,
        //    que também tem um botão "Remove from favorites", precisa já ter saído da composição).
        compose.onNodeWithText(recipe).performClick()
        compose.waitUntil(TIMEOUT_MS) { textExists("Ingredients") && !textExists("Favorites") }
        compose.onNodeWithContentDescription("Remove from favorites").performClick()
        compose.waitUntil(TIMEOUT_MS) { contentDescriptionExists("Add to favorites") }

        // 6. Volta para Favoritos, agora vazio.
        compose.onNodeWithContentDescription("Back").performClick()
        compose.waitUntil(TIMEOUT_MS) { textExists("No favorites yet") }

        // 7. Aba Explorar: a lista continua lá.
        compose.onNodeWithText("Explore").performClick()
        compose.waitUntil(TIMEOUT_MS) { textExists("What are we cooking today?") && textExists(recipe) }
    }

    private fun contentDescriptionExists(description: String) =
        compose.onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()

    private companion object {
        const val TIMEOUT_MS = 10_000L
    }
}
