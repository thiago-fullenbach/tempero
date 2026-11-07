package br.com.thiago.tempero.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import br.com.thiago.tempero.feature.search.SearchEntry
import br.com.thiago.tempero.feature.search.SearchEvent
import kotlinx.serialization.Serializable

/** Rota type-safe da tela Busca (tela cheia, fora da barra inferior). */
@Serializable
data object SearchRoute

fun NavController.navigateToSearch(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SearchRoute, builder)
}

fun NavGraphBuilder.searchScreen(onEvent: (SearchEvent) -> Unit) {
    composable<SearchRoute> {
        SearchEntry(onEvent = onEvent)
    }
}
