package br.com.thiago.tempero.feature.favorites

import br.com.thiago.tempero.core.model.Meal

sealed interface FavoritesIntent {
    data class Remove(val meal: Meal) : FavoritesIntent
    data object UndoRemove : FavoritesIntent

    /** O snackbar de remoção foi dispensado (ou virou "Desfazer"); limpa a receita guardada. */
    data object RemovalMessageShown : FavoritesIntent
    data object Retry : FavoritesIntent
}
