package br.com.thiago.tempero.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.presentation.screen.explore.ExploreEvent
import br.com.thiago.tempero.presentation.screen.search.SearchEvent

/**
 * Coordinator da navegação: as telas emitem eventos semânticos ([ExploreEvent],
 * [SearchEvent]) e é aqui que se decide para onde ir, mantendo [NavHostController]
 * e rotas fora das telas.
 */
@Stable
class TemperoCoordinator(val navController: NavHostController) {

    fun onEvent(event: ExploreEvent) {
        when (event) {
            ExploreEvent.SearchRequested ->
                navController.navigate(TemperoDestination.Search) { launchSingleTop = true }

            is ExploreEvent.MealSelected -> openMeal(event.meal)
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.Dismissed,
            SearchEvent.ExploreCategoriesRequested -> navController.popBackStack()

            is SearchEvent.MealSelected -> openMeal(event.meal)
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
