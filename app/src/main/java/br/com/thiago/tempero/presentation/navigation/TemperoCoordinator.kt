package br.com.thiago.tempero.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.presentation.screen.explore.ExploreEvent
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesEvent
import br.com.thiago.tempero.presentation.screen.search.SearchEvent

/**
 * Coordinator da navegação: as telas emitem eventos semânticos ([ExploreEvent],
 * [SearchEvent], [FavoritesEvent]) e é aqui que se decide para onde ir, mantendo [NavHostController]
 * e rotas fora das telas.
 */
@Stable
class TemperoCoordinator(val navController: NavHostController) {

    fun onEvent(event: ExploreEvent) {
        when (event) {
            ExploreEvent.SearchRequested -> runIfResumed {
                navController.navigate(TemperoDestination.Search) { launchSingleTop = true }
            }

            ExploreEvent.FavoritesRequested -> onTabSelected(TopLevelDestination.Favorites)

            is ExploreEvent.MealSelected -> openMeal(event.meal)
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.ExploreRequested -> onTabSelected(TopLevelDestination.Explore)
            is FavoritesEvent.MealSelected -> openMeal(event.meal)
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.Dismissed -> runIfResumed { navController.popBackStack() }

            // Pede "ir para o Explorar", não "voltar um passo": nunca remove o próprio Explorar.
            SearchEvent.ExploreCategoriesRequested -> runIfResumed {
                navController.popBackStack(TemperoDestination.Explore, inclusive = false)
            }

            is SearchEvent.MealSelected -> openMeal(event.meal)
        }
    }

    /** Troca de aba da barra inferior, guardando e restaurando o estado de cada uma. */
    fun onTabSelected(destination: TopLevelDestination) = runIfResumed {
        navController.navigate(destination.route) {
            popUpTo(TemperoDestination.Explore) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    /**
     * Só navega se a tela atual está [Lifecycle.State.RESUMED]. Durante a animação de saída a
     * tela antiga ainda é clicável, mas já saiu desse estado; sem a guarda, um toque repetido
     * executaria a navegação duas vezes (ex.: dois `popBackStack()` esvaziam a pilha e deixam
     * a tela em branco).
     */
    private inline fun runIfResumed(block: () -> Unit) {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            block()
        }
    }

    /** Ainda não há tela de detalhe da receita; sem efeito por enquanto. */
    @Suppress("UNUSED_PARAMETER")
    private fun openMeal(meal: Meal) {
        // TODO: navegar para o detalhe da receita
    }
}

@Composable
fun rememberTemperoCoordinator(
    navController: NavHostController = rememberNavController()
): TemperoCoordinator = remember(navController) { TemperoCoordinator(navController) }
