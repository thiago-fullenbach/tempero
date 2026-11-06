package br.com.thiago.tempero.presentation.navigation

import kotlinx.serialization.Serializable

/** Destinos do grafo de navegação, usados como rotas type-safe. */
sealed interface TemperoDestination {
    @Serializable
    data object Explore : TemperoDestination

    @Serializable
    data object Search : TemperoDestination
}
