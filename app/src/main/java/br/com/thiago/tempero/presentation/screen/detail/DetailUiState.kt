package br.com.thiago.tempero.presentation.screen.detail

import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Error(val error: DataError) : DetailUiState

    /** A API respondeu, mas a receita não existe (`{"meals": null}`). */
    data object NotFound : DetailUiState
    data class Success(val meal: Meal, val isFavorite: Boolean) : DetailUiState
}
