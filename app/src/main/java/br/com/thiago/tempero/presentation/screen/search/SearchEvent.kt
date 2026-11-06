package br.com.thiago.tempero.presentation.screen.search

import br.com.thiago.tempero.domain.model.Meal

/**
 * Eventos que a tela Busca emite para fora dela. Descrevem o que o usuário fez,
 * não para onde ir; quem decide o destino é o coordinator.
 */
sealed interface SearchEvent {
    data object Dismissed : SearchEvent
    data object ExploreCategoriesRequested : SearchEvent
    data class MealSelected(val meal: Meal) : SearchEvent
}
