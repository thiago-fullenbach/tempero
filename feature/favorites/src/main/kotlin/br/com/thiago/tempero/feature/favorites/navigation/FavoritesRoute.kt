package br.com.thiago.tempero.feature.favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import br.com.thiago.tempero.feature.favorites.FavoritesEntry
import br.com.thiago.tempero.feature.favorites.FavoritesEvent
import kotlinx.serialization.Serializable

/** Rota type-safe da tela Favoritos (aba da barra inferior). */
@Serializable
data object FavoritesRoute

fun NavController.navigateToFavorites(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(FavoritesRoute, builder)
}

fun NavGraphBuilder.favoritesScreen(onEvent: (FavoritesEvent) -> Unit) {
    composable<FavoritesRoute> {
        FavoritesEntry(onEvent = onEvent)
    }
}
