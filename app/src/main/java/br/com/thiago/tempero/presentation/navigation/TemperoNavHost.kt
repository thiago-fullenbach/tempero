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
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.thiago.tempero.core.domain.analytics.AnalyticsTracker
import br.com.thiago.tempero.feature.detail.navigation.detailScreen
import br.com.thiago.tempero.feature.explore.navigation.ExploreRoute
import br.com.thiago.tempero.feature.explore.navigation.exploreScreen
import br.com.thiago.tempero.feature.favorites.navigation.FavoritesRoute
import br.com.thiago.tempero.feature.favorites.navigation.favoritesScreen
import br.com.thiago.tempero.feature.search.navigation.searchScreen

/**
 * Grafo de navegação do app; toda decisão de destino passa pelo [coordinator].
 * A barra inferior só aparece nos destinos de [TopLevelDestination] (Busca e Detalhe são tela cheia).
 */
@Composable
fun TemperoNavHost(
    analytics: AnalyticsTracker,
    modifier: Modifier = Modifier,
    coordinator: TemperoCoordinator = rememberTemperoCoordinator(analytics = analytics)
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
            startDestination = ExploreRoute,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            exploreScreen(onEvent = coordinator::onEvent)
            searchScreen(onEvent = coordinator::onEvent)
            favoritesScreen(onEvent = coordinator::onEvent)
            detailScreen(onEvent = coordinator::onEvent)
        }
    }
}

private fun NavDestination.isTab(tab: TopLevelDestination): Boolean = when (tab) {
    TopLevelDestination.Explore -> hasRoute<ExploreRoute>()
    TopLevelDestination.Favorites -> hasRoute<FavoritesRoute>()
}
