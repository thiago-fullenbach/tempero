package br.com.thiago.tempero.feature.favorites

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Error(val error: DataError) : FavoritesUiState

    /** Nenhum favorito. Guarda a última remoção para o "Desfazer" valer também ao remover o último item. */
    data class Empty(val recentlyRemoved: Meal? = null) : FavoritesUiState

    /** [recentlyRemoved] é a receita que acabou de sair da lista, enquanto o snackbar "Desfazer" é exibido. */
    data class Success(
        val meals: List<Meal>,
        val recentlyRemoved: Meal? = null
    ) : FavoritesUiState
}

/** Receita removida há pouco, quando o estado a carrega. */
val FavoritesUiState.recentlyRemoved: Meal?
    get() = when (this) {
        is FavoritesUiState.Success -> recentlyRemoved
        is FavoritesUiState.Empty -> recentlyRemoved
        else -> null
    }
