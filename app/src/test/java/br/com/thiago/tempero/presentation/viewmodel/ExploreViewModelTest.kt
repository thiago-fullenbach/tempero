package br.com.thiago.tempero.presentation.viewmodel

import androidx.paging.testing.asSnapshot
import br.com.thiago.tempero.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.fake.FakeMealRepository
import br.com.thiago.tempero.presentation.screen.explore.ExploreIntent
import br.com.thiago.tempero.presentation.screen.explore.ExploreUiState
import br.com.thiago.tempero.testutil.MainDispatcherRule
import br.com.thiago.tempero.testutil.meal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ExploreViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val beef = MealCategory("Beef")
    private val chicken = MealCategory("Chicken")
    private val repository = FakeMealRepository().apply {
        categories = listOf(beef, chicken)
        meals = listOf(
            meal(1, "Beef Pie", category = "Beef"),
            meal(2, "Chicken Soup", category = "Chicken"),
            meal(3, "Beef Stew", category = "Beef")
        )
    }
    private val analytics = FakeAnalyticsTracker()

    private fun viewModel() = ExploreViewModel(repository, analytics)

    @Test
    fun `carrega categorias e receitas`() {
        val vm = viewModel()

        assertEquals(ExploreUiState.Success(listOf(beef, chicken), selected = null), vm.uiState.value)
        assertEquals(1, repository.listCategoriesCalls)
        assertEquals(1, repository.getAllMealsCalls)
    }

    @Test
    fun `falha ao carregar mostra erro e registra load_failed`() {
        val cause = DataError.Network(IOException())
        repository.error = cause

        val vm = viewModel()

        assertEquals(ExploreUiState.Error(cause), vm.uiState.value)
        val event = analytics.eventsOf<AnalyticsEvent.LoadFailed>().single()
        assertEquals(AnalyticsEvent.SCREEN_EXPLORE, event.screen)
    }

    @Test
    fun `Retry recarrega depois de um erro`() {
        repository.error = DataError.Network(IOException())
        val vm = viewModel()
        repository.error = null

        vm.onHandleIntent(ExploreIntent.Retry)

        assertTrue(vm.uiState.value is ExploreUiState.Success)
        assertEquals(2, repository.listCategoriesCalls)
    }

    @Test
    fun `SelectCategory atualiza a selecao e registra o evento`() {
        val vm = viewModel()

        vm.onHandleIntent(ExploreIntent.SelectCategory(beef))
        assertEquals(beef, (vm.uiState.value as ExploreUiState.Success).selected)

        vm.onHandleIntent(ExploreIntent.SelectCategory(null))
        assertEquals(null, (vm.uiState.value as ExploreUiState.Success).selected)

        assertEquals(
            listOf("Beef", null),
            analytics.eventsOf<AnalyticsEvent.CategorySelected>().map { it.category }
        )
    }

    @Test
    fun `SelectCategory e ignorado enquanto nao ha sucesso`() {
        repository.error = DataError.Network(IOException())
        val vm = viewModel()

        vm.onHandleIntent(ExploreIntent.SelectCategory(beef))

        assertTrue(vm.uiState.value is ExploreUiState.Error)
    }

    @Test
    fun `lista paginada traz todas as receitas por padrao`() = runTest {
        val vm = viewModel()

        val items = vm.meals.asSnapshot()

        assertEquals(listOf(1L, 2L, 3L), items.map { it.id })
    }

    @Test
    fun `lista paginada filtra pela categoria selecionada`() = runTest {
        val vm = viewModel()
        vm.onHandleIntent(ExploreIntent.SelectCategory(beef))

        val items = vm.meals.asSnapshot()

        assertEquals(listOf(1L, 3L), items.map { it.id })
    }
}
