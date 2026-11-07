package br.com.thiago.tempero.feature.detail

import androidx.lifecycle.SavedStateHandle
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.domain.usecase.GetMealDetailUseCase
import br.com.thiago.tempero.core.domain.usecase.ToggleFavoriteUseCase
import br.com.thiago.tempero.core.testing.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.core.testing.fake.FakeFavoriteRepository
import br.com.thiago.tempero.core.testing.fake.FakeMealRepository
import br.com.thiago.tempero.core.testing.util.MainDispatcherRule
import br.com.thiago.tempero.core.testing.util.completeMeal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

/** Robolectric porque `SavedStateHandle.toRoute` monta um `Bundle` do Android. */
@RunWith(RobolectricTestRunner::class)
class DetailViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val meal = completeMeal(id = 1, name = "Pie")
    private val favorites = FakeFavoriteRepository()
    private val meals = FakeMealRepository().apply { byId[1] = meal }
    private val analytics = FakeAnalyticsTracker()

    private fun viewModel() = DetailViewModel(
        getMealDetail = GetMealDetailUseCase(favorites, meals),
        toggleFavorite = ToggleFavoriteUseCase(favorites, meals),
        favoriteRepository = favorites,
        analytics = analytics,
        savedStateHandle = SavedStateHandle(mapOf("mealId" to 1L))
    )

    @Test
    fun `carrega a receita da API e registra view_item`() {
        val vm = viewModel()

        assertEquals(DetailUiState.Success(meal, isFavorite = false), vm.uiState.value)
        assertEquals(1L, meals.getByIdCalls.single())
        assertEquals(meal, analytics.eventsOf<AnalyticsEvent.ViewItem>().single().meal)
    }

    @Test
    fun `receita favorita vem do Room sem chamar a API`() {
        favorites.store.value = mapOf(1L to meal)

        val vm = viewModel()

        assertEquals(DetailUiState.Success(meal, isFavorite = true), vm.uiState.value)
        assertTrue(meals.getByIdCalls.isEmpty())
    }

    @Test
    fun `receita inexistente mostra NotFound`() {
        meals.byId.clear()

        assertEquals(DetailUiState.NotFound, viewModel().uiState.value)
    }

    @Test
    fun `falha ao carregar mostra Error e registra load_failed`() {
        val cause = DataError.Network(IOException())
        meals.error = cause

        val vm = viewModel()

        assertEquals(DetailUiState.Error(cause), vm.uiState.value)
        assertEquals(AnalyticsEvent.SCREEN_DETAIL, analytics.eventsOf<AnalyticsEvent.LoadFailed>().single().screen)
    }

    @Test
    fun `Retry recarrega depois de um erro`() {
        meals.error = DataError.Network(IOException())
        val vm = viewModel()
        meals.error = null

        vm.onHandleIntent(DetailIntent.Retry)

        assertEquals(DetailUiState.Success(meal, isFavorite = false), vm.uiState.value)
    }

    @Test
    fun `ToggleFavorite favorita e desfavorita refletindo no estado`() {
        val vm = viewModel()

        vm.onHandleIntent(DetailIntent.ToggleFavorite)
        assertEquals(DetailUiState.Success(meal, isFavorite = true), vm.uiState.value)
        assertEquals(meal, favorites.store.value[1])
        val added = analytics.eventsOf<AnalyticsEvent.FavoriteAdded>().single()
        assertEquals(AnalyticsEvent.ORIGIN_DETAIL, added.origin)

        vm.onHandleIntent(DetailIntent.ToggleFavorite)
        assertEquals(DetailUiState.Success(meal, isFavorite = false), vm.uiState.value)
        val removed = analytics.eventsOf<AnalyticsEvent.FavoriteRemoved>().single()
        assertEquals(1L, removed.mealId)
        assertEquals(AnalyticsEvent.ORIGIN_DETAIL, removed.origin)
    }

    @Test
    fun `erro ao favoritar mostra Error`() {
        val vm = viewModel()
        val cause = DataError.Storage(IllegalStateException())
        favorites.writeError = cause

        vm.onHandleIntent(DetailIntent.ToggleFavorite)

        assertEquals(DetailUiState.Error(cause), vm.uiState.value)
    }

    @Test
    fun `ToggleFavorite sem receita carregada e ignorado`() {
        meals.byId.clear()
        val vm = viewModel()

        vm.onHandleIntent(DetailIntent.ToggleFavorite)

        assertEquals(DetailUiState.NotFound, vm.uiState.value)
        assertTrue(favorites.store.value.isEmpty())
    }
}
