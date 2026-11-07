package br.com.thiago.tempero.feature.explore.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import br.com.thiago.tempero.feature.explore.ExploreEntry
import br.com.thiago.tempero.feature.explore.ExploreEvent
import kotlinx.serialization.Serializable

/** Rota type-safe da tela Explorar (destino inicial e aba da barra inferior). */
@Serializable
data object ExploreRoute

fun NavController.navigateToExplore(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(ExploreRoute, builder)
}

fun NavGraphBuilder.exploreScreen(onEvent: (ExploreEvent) -> Unit) {
    composable<ExploreRoute> {
        ExploreEntry(onEvent = onEvent)
    }
}
