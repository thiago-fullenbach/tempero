package br.com.thiago.tempero.feature.explore

import br.com.thiago.tempero.core.model.MealCategory

sealed interface ExploreIntent {
    object Retry: ExploreIntent
    data class SelectCategory(val category: MealCategory?): ExploreIntent
}