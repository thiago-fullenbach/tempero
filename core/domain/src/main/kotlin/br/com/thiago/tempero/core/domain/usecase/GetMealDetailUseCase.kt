package br.com.thiago.tempero.core.domain.usecase

import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.domain.repository.FavoriteRepository
import br.com.thiago.tempero.core.domain.repository.MealRepository
import jakarta.inject.Inject

/** Detalhe da receita: usa o favorito salvo no Room (funciona offline) e, se não houver, busca na API. */
class GetMealDetailUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val mealRepository: MealRepository
) {
    /** `null` quando a receita não existe. Falhas chegam como [br.com.thiago.tempero.core.model.DataError]. */
    suspend operator fun invoke(id: Long): Meal? =
        favoriteRepository.getFavorite(id) ?: mealRepository.getMealById(id)
}
