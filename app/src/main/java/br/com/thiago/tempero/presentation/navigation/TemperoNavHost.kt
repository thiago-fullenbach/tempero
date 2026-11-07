package br.com.thiago.tempero.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.thiago.tempero.presentation.screen.detail.DetailRoute
import br.com.thiago.tempero.presentation.screen.explore.ExploreRoute
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesRoute
import br.com.thiago.tempero.presentation.screen.search.SearchRoute

/**
 * Grafo de navegação do app; toda decisão de destino passa pelo [coordinator].
 * A barra inferior só aparece nos destinos de [TopLevelDestination] (Busca e Detalhe são tela cheia).
 */
@Composable
fun TemperoNavHost(
    modifier: Modifier = Modifier,
    coordinator: TemperoCoordinator = rememberTemperoCoordinator()
) {
    val backStackEntry by coordinator.navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentTab = TopLevelDestination.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.isTab(tab) } == true
    }

    Scaffold(
        modifier = modifier,
        // As telas tratam a status bar sozinhas; aqui só entra a barra inferior.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (currentTab != null) {
                TemperoBottomBar(
                    selected = currentTab,
                    onDestinationSelected = coordinator::onTabSelected
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = coordinator.navController,
            startDestination = TemperoDestination.Explore,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable<TemperoDestination.Explore> {
                ExploreRoute(onEvent = coordinator::onEvent)
            }
            composable<TemperoDestination.Search> {
                SearchRoute(onEvent = coordinator::onEvent)
            }
            composable<TemperoDestination.Favorites> {
                FavoritesRoute(onEvent = coordinator::onEvent)
            }
            composable<TemperoDestination.Detail> {
                DetailRoute(onEvent = coordinator::onEvent)
            }
        }
    }
}

private fun NavDestination.isTab(tab: TopLevelDestination): Boolean = when (tab) {
    TopLevelDestination.Explore -> hasRoute<TemperoDestination.Explore>()
    TopLevelDestination.Favorites -> hasRoute<TemperoDestination.Favorites>()
}
