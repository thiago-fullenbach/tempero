package br.com.thiago.tempero.presentation.screen.explore

import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.MealCategory

sealed interface ExploreUiState {
    data object Loading : ExploreUiState
    data class Error(val error: DataError) : ExploreUiState
    data class Success(
        val categories: List<MealCategory>,
        val selected: MealCategory? = null,
    ) : ExploreUiState
}
