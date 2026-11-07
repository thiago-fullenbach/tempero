package br.com.thiago.tempero.feature.search

import androidx.lifecycle.SavedStateHandle
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.testing.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.core.testing.fake.FakeMealRepository
import br.com.thiago.tempero.core.testing.util.MainDispatcherRule
import br.com.thiago.tempero.core.testing.util.meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule(StandardTestDispatcher())

    private val repository = FakeMealRepository().apply { searchResults = listOf(meal(1, "Pasta")) }
    private val analytics = FakeAnalyticsTracker()

    private fun viewModel(handle: SavedStateHandle = SavedStateHandle()) =
        SearchViewModel(repository, handle, analytics)

    @Test
    fun `comeca ocioso e sem chamar a API`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        assertEquals(SearchUiState.Idle, vm.uiState.value)
        assertTrue(repository.searchQueries.isEmpty())
    }

    @Test
    fun `consulta com menos de 2 letras fica ociosa`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("a"))
        advanceUntilIdle()

        assertEquals(SearchUiState.Idle, vm.uiState.value)
        assertTrue(repository.searchQueries.isEmpty())
    }

    @Test
    fun `busca so dispara depois do debounce de 400 ms`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceTimeBy(399)
        runCurrent()
        assertTrue(repository.searchQueries.isEmpty())

        advanceTimeBy(2)
        runCurrent()
        assertEquals(listOf("pasta"), repository.searchQueries)
        assertEquals(SearchUiState.Success(listOf(meal(1, "Pasta"))), vm.uiState.value)
    }

    @Test
    fun `digitar de novo reinicia o debounce`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("pa"))
        advanceTimeBy(300)
        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceUntilIdle()

        assertEquals(listOf("pasta"), repository.searchQueries)
    }

    @Test
    fun `faz trim da consulta`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("  pasta "))
        advanceUntilIdle()

        assertEquals(listOf("pasta"), repository.searchQueries)
    }

    @Test
    fun `sem resultados mostra Empty`() = runTest {
        repository.searchResults = emptyList()
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("zzz"))
        advanceUntilIdle()

        assertEquals(SearchUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `falha mostra erro e registra load_failed`() = runTest {
        val cause = DataError.Network(IOException())
        repository.error = cause
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceUntilIdle()

        assertEquals(SearchUiState.Error(cause), vm.uiState.value)
        assertEquals(AnalyticsEvent.SCREEN_SEARCH, analytics.eventsOf<AnalyticsEvent.LoadFailed>().single().screen)
    }

    @Test
    fun `Retry repete a busca depois de um erro`() = runTest {
        repository.error = DataError.Network(IOException())
        val vm = viewModel()
        runCurrent()
        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceUntilIdle()
        repository.error = null

        vm.onHandleIntent(SearchIntent.Retry)
        advanceUntilIdle()

        assertTrue(vm.uiState.value is SearchUiState.Success)
    }

    @Test
    fun `sugestao busca na hora e registra a busca uma unica vez`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.SuggestionSelected("chicken"))
        runCurrent()
        // Antes do debounce a busca já aconteceu.
        assertEquals(listOf("chicken"), repository.searchQueries)
        assertTrue(vm.uiState.value is SearchUiState.Success)

        // O debounce da query dispara outra busca, mas o analytics não conta de novo.
        advanceUntilIdle()
        val searches = analytics.eventsOf<AnalyticsEvent.Search>()
        assertEquals(1, searches.size)
        assertEquals(AnalyticsEvent.METHOD_SUGGESTION, searches.single().method)
        assertEquals(1, searches.single().resultsCount)
    }

    @Test
    fun `busca digitada registra o metodo typed`() = runTest {
        val vm = viewModel()
        runCurrent()

        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceUntilIdle()

        assertEquals(AnalyticsEvent.METHOD_TYPED, analytics.eventsOf<AnalyticsEvent.Search>().single().method)
    }

    @Test
    fun `Clear limpa a consulta e volta a Idle`() = runTest {
        val vm = viewModel()
        runCurrent()
        vm.onHandleIntent(SearchIntent.QueryChanged("pasta"))
        advanceUntilIdle()

        vm.onHandleIntent(SearchIntent.Clear)
        advanceUntilIdle()

        assertEquals("", vm.query.value)
        assertEquals(SearchUiState.Idle, vm.uiState.value)
    }

    @Test
    fun `consulta e restaurada do SavedStateHandle`() = runTest {
        val vm = viewModel(SavedStateHandle(mapOf("query" to "soup")))
        advanceUntilIdle()

        assertEquals("soup", vm.query.value)
        assertEquals(listOf("soup"), repository.searchQueries)
    }
}
