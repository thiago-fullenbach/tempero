package br.com.thiago.tempero.feature.detail

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Error(val error: DataError) : DetailUiState

    /** A API respondeu, mas a receita não existe (`{"meals": null}`). */
    data object NotFound : DetailUiState
    data class Success(val meal: Meal, val isFavorite: Boolean) : DetailUiState
}
