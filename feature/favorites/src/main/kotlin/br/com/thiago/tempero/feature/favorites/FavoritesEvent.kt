package br.com.thiago.tempero.feature.favorites

import br.com.thiago.tempero.core.model.Meal

/**
 * Eventos que a tela Favoritos emite para fora dela. Descrevem o que o usuário fez,
 * não para onde ir; quem decide o destino é o coordinator.
 */
sealed interface FavoritesEvent {
    data object ExploreRequested : FavoritesEvent
    data class MealSelected(val meal: Meal) : FavoritesEvent
}
