package br.com.thiago.tempero.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.thiago.tempero.presentation.screen.explore.ExploreRoute
import br.com.thiago.tempero.presentation.screen.search.SearchRoute

/** Grafo de navegação do app; toda decisão de destino passa pelo [coordinator]. */
@Composable
fun TemperoNavHost(
    modifier: Modifier = Modifier,
    coordinator: TemperoCoordinator = rememberTemperoCoordinator()
) {
    NavHost(
        navController = coordinator.navController,
        startDestination = TemperoDestination.Explore,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
        modifier = modifier
    ) {
        composable<TemperoDestination.Explore> {
            ExploreRoute(onEvent = coordinator::onEvent)
        }
        composable<TemperoDestination.Search> {
            SearchRoute(onEvent = coordinator::onEvent)
        }
    }
}
