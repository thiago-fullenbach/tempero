package br.com.thiago.tempero.core.domain.usecase

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.domain.repository.FavoriteRepository
import br.com.thiago.tempero.core.domain.repository.MealRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Alterna o favorito. Ao adicionar, garante que a receita completa vá para o Room
 * (o detalhe precisa funcionar offline): itens do `filter.php` vêm sem ingredientes
 * nem preparo, e nesse caso a receita é buscada na API antes de salvar.
 * Retorna o novo estado (`true` = favorita).
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(meal: Meal): Boolean {
        // O estado atual vem do Room (fonte da verdade), não da UI: toques rápidos não invertem a ação.
        if (favoriteRepository.observeIsFavorite(meal.id).first()) {
            favoriteRepository.removeFavorite(meal.id)
            return false
        }
        val complete = if (meal.isComplete) {
            meal
        } else {
            mealRepository.getMealById(meal.id)
                ?: throw DataError.InvalidResponse(
                    IllegalStateException("Receita ${meal.id} não encontrada para favoritar")
                )
        }
        favoriteRepository.addFavorite(complete)
        return true
    }

    /** Itens do `filter.php` chegam sem ingredientes nem preparo. */
    private val Meal.isComplete: Boolean
        get() = ingredients.isNotEmpty() || steps.isNotEmpty()
}
