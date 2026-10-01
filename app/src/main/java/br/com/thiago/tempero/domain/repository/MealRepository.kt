package br.com.thiago.tempero.domain.repository

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory

interface MealRepository {
    suspend fun searchMealsByFirstLetter(firstLetter: Char): List<Meal>

    suspend fun listCategories(): List<MealCategory>
}