package br.com.thiago.tempero.presentation.screen.explore

import br.com.thiago.tempero.domain.model.Meal

/**
 * Eventos que a tela Explorar emite para fora dela. Descrevem o que o usuário fez,
 * não para onde ir; quem decide o destino é o coordinator.
 */
sealed interface ExploreEvent {
    data object SearchRequested : ExploreEvent
    data class MealSelected(val meal: Meal) : ExploreEvent
}
