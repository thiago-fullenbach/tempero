package br.com.thiago.tempero.domain.repository

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory

interface MealRepository {
    suspend fun getAllMeals(categories: List<MealCategory>): List<Meal>

    suspend fun searchMeals(query: String): List<Meal>

    /** Receita completa (ingredientes e preparo) via `lookup.php`; `null` se o id não existe. */
    suspend fun getMealById(id: Long): Meal?

    suspend fun listCategories(): List<MealCategory>
}