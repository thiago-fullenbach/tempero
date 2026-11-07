package br.com.thiago.tempero.feature.search

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal

sealed interface SearchUiState {
    /** Campo vazio: categorias e sugestões fixas, sem chamadas à API. */
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Error(val error: DataError) : SearchUiState
    data object Empty : SearchUiState
    data class Success(val meals: List<Meal>) : SearchUiState
}
