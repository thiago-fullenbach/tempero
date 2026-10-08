package br.com.thiago.tempero.presentation.screen.explore

import br.com.thiago.tempero.domain.model.MealCategory

sealed interface ExploreIntent {
    object Retry: ExploreIntent
    data class SelectCategory(val category: MealCategory?): ExploreIntent
}