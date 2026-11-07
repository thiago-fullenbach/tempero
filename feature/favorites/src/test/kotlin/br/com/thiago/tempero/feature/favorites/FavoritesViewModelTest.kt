package br.com.thiago.tempero.feature.favorites

import app.cash.turbine.test
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.testing.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.core.testing.fake.FakeConnectivityObserver
import br.com.thiago.tempero.core.testing.fake.FakeFavoriteRepository
import br.com.thiago.tempero.core.testing.util.MainDispatcherRule
import br.com.thiago.tempero.core.testing.util.completeMeal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val pie = completeMeal(1, "Pie")
    private val apple = completeMeal(2, "Apple")
    private val repository = FakeFavoriteRepository()
    private val analytics = FakeAnalyticsTracker()
    private val connectivity = FakeConnectivityObserver()

    private fun viewModel() = FavoritesViewModel(repository, analytics, connectivity)

    @Test
    fun `sem favoritos o estado e Empty`() {
        assertEquals(FavoritesUiState.Empty(), viewModel().uiState.value)
    }

    @Test
    fun `com favoritos o estado e Success ordenado por nome`() {
        repository.store.value = listOf(pie, apple).associateBy { it.id }

        assertEquals(FavoritesUiState.Success(listOf(apple, pie)), viewModel().uiState.value)
    }

    @Test
    fun `a lista acompanha mudancas no repositorio`() = runTest {
        val vm = viewModel()

        repository.addFavorite(pie)

        assertEquals(FavoritesUiState.Success(listOf(pie)), vm.uiState.value)
    }

    @Test
    fun `Remove tira da lista, guarda para desfazer e registra o evento`() {
        repository.store.value = mapOf(1L to pie, 2L to apple)
        val vm = viewModel()

        vm.onHandleIntent(FavoritesIntent.Remove(pie))

        assertEquals(FavoritesUiState.Success(listOf(apple), recentlyRemoved = pie), vm.uiState.value)
        val event = analytics.eventsOf<AnalyticsEvent.FavoriteRemoved>().single()
        assertEquals(1L, event.mealId)
        assertEquals(AnalyticsEvent.ORIGIN_FAVORITES, event.origin)
    }

    @Test
    fun `remover o ultimo favorito mantem o desfazer no estado Empty`() {
        repository.store.value = mapOf(1L to pie)
        val vm = viewModel()

        vm.onHandleIntent(FavoritesIntent.Remove(pie))

        assertEquals(FavoritesUiState.Empty(recentlyRemoved = pie), vm.uiState.value)
    }

    @Test
    fun `UndoRemove devolve a receita e registra o evento`() {
        repository.store.value = mapOf(1L to pie)
        val vm = viewModel()
        vm.onHandleIntent(FavoritesIntent.Remove(pie))

        vm.onHandleIntent(FavoritesIntent.UndoRemove)

        assertEquals(FavoritesUiState.Success(listOf(pie)), vm.uiState.value)
        assertEquals(1L, analytics.eventsOf<AnalyticsEvent.FavoriteRemovalUndone>().single().mealId)
    }

    @Test
    fun `UndoRemove sem remocao recente nao faz nada`() {
        val vm = viewModel()

        vm.onHandleIntent(FavoritesIntent.UndoRemove)

        assertEquals(FavoritesUiState.Empty(), vm.uiState.value)
        assertTrue(analytics.events.isEmpty())
    }

    @Test
    fun `RemovalMessageShown limpa a receita guardada`() {
        repository.store.value = mapOf(1L to pie)
        val vm = viewModel()
        vm.onHandleIntent(FavoritesIntent.Remove(pie))

        vm.onHandleIntent(FavoritesIntent.RemovalMessageShown)

        assertEquals(FavoritesUiState.Empty(), vm.uiState.value)
    }

    @Test
    fun `erro ao remover mostra Error`() {
        repository.store.value = mapOf(1L to pie)
        val vm = viewModel()
        val cause = DataError.Storage(IllegalStateException())
        repository.writeError = cause

        vm.onHandleIntent(FavoritesIntent.Remove(pie))

        assertEquals(FavoritesUiState.Error(cause), vm.uiState.value)
    }

    @Test
    fun `falha ao observar vira Error e Retry se recupera`() {
        repository.observeError = IllegalStateException("banco")
        val vm = viewModel()

        val state = vm.uiState.value
        assertTrue(state is FavoritesUiState.Error)
        // Exceção que não é DataError é embrulhada em Unknown.
        assertTrue((state as FavoritesUiState.Error).error is DataError.Unknown)

        repository.observeError = null
        vm.onHandleIntent(FavoritesIntent.Retry)

        assertEquals(FavoritesUiState.Empty(), vm.uiState.value)
    }

    @Test
    fun `isOffline e o inverso da conectividade`() = runTest {
        val vm = viewModel()

        vm.isOffline.test {
            assertEquals(false, awaitItem())

            connectivity.online.value = false
            assertEquals(true, awaitItem())

            connectivity.online.value = true
            assertEquals(false, awaitItem())
        }
    }
}
