package br.com.thiago.tempero.core.domain.repository

import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory

interface MealRepository {
    suspend fun getAllMeals(categories: List<MealCategory>): List<Meal>

    suspend fun searchMeals(query: String): List<Meal>

    /** Receita completa (ingredientes e preparo) via `lookup.php`; `null` se o id não existe. */
    suspend fun getMealById(id: Long): Meal?

    suspend fun listCategories(): List<MealCategory>
}
