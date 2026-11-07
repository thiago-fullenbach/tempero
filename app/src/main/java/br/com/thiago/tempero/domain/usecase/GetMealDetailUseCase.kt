package br.com.thiago.tempero.domain.usecase

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import br.com.thiago.tempero.domain.repository.MealRepository
import jakarta.inject.Inject

/** Detalhe da receita: usa o favorito salvo no Room (funciona offline) e, se não houver, busca na API. */
class GetMealDetailUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val mealRepository: MealRepository
) {
    /** `null` quando a receita não existe. Falhas chegam como [br.com.thiago.tempero.domain.error.DataError]. */
    suspend operator fun invoke(id: Long): Meal? =
        favoriteRepository.getFavorite(id) ?: mealRepository.getMealById(id)
}
