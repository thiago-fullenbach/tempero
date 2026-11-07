package br.com.thiago.tempero.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.domain.analytics.AnalyticsTracker
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.feature.detail.DetailEvent
import br.com.thiago.tempero.feature.explore.ExploreEvent
import br.com.thiago.tempero.feature.favorites.FavoritesEvent
import br.com.thiago.tempero.feature.search.SearchEvent
import br.com.thiago.tempero.feature.detail.navigation.DetailRoute
import br.com.thiago.tempero.feature.detail.navigation.navigateToDetail
import br.com.thiago.tempero.feature.explore.navigation.ExploreRoute
import br.com.thiago.tempero.feature.favorites.navigation.FavoritesRoute
import br.com.thiago.tempero.feature.search.navigation.SearchRoute
import br.com.thiago.tempero.feature.search.navigation.navigateToSearch
import timber.log.Timber

/**
 * Coordinator da navegação: as telas emitem eventos semânticos ([ExploreEvent],
 * [SearchEvent], [FavoritesEvent], [DetailEvent]) e é aqui que se decide para onde ir, mantendo [NavHostController]
 * e rotas fora das telas.
 */
@Stable
class TemperoCoordinator(
    val navController: NavHostController,
    private val analytics: AnalyticsTracker
) {

    fun onEvent(event: ExploreEvent) {
        when (event) {
            ExploreEvent.SearchRequested -> runIfResumed {
                navController.navigateToSearch { launchSingleTop = true }
            }

            ExploreEvent.FavoritesRequested -> onTabSelected(TopLevelDestination.Favorites)

            is ExploreEvent.MealSelected -> openMeal(event.meal, AnalyticsEvent.ORIGIN_EXPLORE)
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.ExploreRequested -> onTabSelected(TopLevelDestination.Explore)
            is FavoritesEvent.MealSelected -> openMeal(event.meal, AnalyticsEvent.ORIGIN_FAVORITES)
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.Dismissed -> runIfResumed { navController.popBackStack() }

            // Pede "ir para o Explorar", não "voltar um passo": nunca remove o próprio Explorar.
            SearchEvent.ExploreCategoriesRequested -> runIfResumed {
                navController.popBackStack(ExploreRoute, inclusive = false)
            }

            is SearchEvent.MealSelected -> openMeal(event.meal, AnalyticsEvent.ORIGIN_SEARCH)
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
            popUpTo(ExploreRoute) { saveState = true }
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

    /** [origin] é a lista de onde a receita foi aberta; só é registrado se a navegação ocorrer. */
    private fun openMeal(meal: Meal, origin: String) = runIfResumed {
        analytics.track(AnalyticsEvent.SelectItem(meal, origin))
        navController.navigateToDetail(meal.id) { launchSingleTop = true }
    }
}

@Composable
fun rememberTemperoCoordinator(
    analytics: AnalyticsTracker,
    navController: NavHostController = rememberNavController()
): TemperoCoordinator {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            Timber.d("Destino: %s", destination.route)
            destination.analyticsScreen()?.let { analytics.track(AnalyticsEvent.ScreenView(it)) }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
    return remember(navController) { TemperoCoordinator(navController, analytics) }
}

/** Nome da tela para o `screen_view`; `null` para destinos desconhecidos. */
private fun NavDestination.analyticsScreen(): String? = when {
    hasRoute<ExploreRoute>() -> AnalyticsEvent.SCREEN_EXPLORE
    hasRoute<SearchRoute>() -> AnalyticsEvent.SCREEN_SEARCH
    hasRoute<FavoritesRoute>() -> AnalyticsEvent.SCREEN_FAVORITES
    hasRoute<DetailRoute>() -> AnalyticsEvent.SCREEN_DETAIL
    else -> null
}
