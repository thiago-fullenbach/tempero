package br.com.thiago.tempero.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.presentation.screen.detail.DetailEvent
import br.com.thiago.tempero.presentation.screen.explore.ExploreEvent
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesEvent
import br.com.thiago.tempero.presentation.screen.search.SearchEvent
import timber.log.Timber

/**
 * Coordinator da navegação: as telas emitem eventos semânticos ([ExploreEvent],
 * [SearchEvent], [FavoritesEvent], [DetailEvent]) e é aqui que se decide para onde ir, mantendo [NavHostController]
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

    fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.BackRequested -> runIfResumed { navController.popBackStack() }
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
        val state = navController.currentBackStackEntry?.lifecycle?.currentState
        if (state == Lifecycle.State.RESUMED) {
            block()
        } else {
            Timber.d("Navegação ignorada: tela atual não está RESUMED (estado=%s)", state)
        }
    }

    private fun openMeal(meal: Meal) = runIfResumed {
        navController.navigate(TemperoDestination.Detail(meal.id)) { launchSingleTop = true }
    }
}

@Composable
fun rememberTemperoCoordinator(
    navController: NavHostController = rememberNavController()
): TemperoCoordinator {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            Timber.d("Destino: %s", destination.route)
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
    return remember(navController) { TemperoCoordinator(navController) }
}
