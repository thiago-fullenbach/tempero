package br.com.thiago.tempero.domain.repository

import br.com.thiago.tempero.domain.model.Meal

interface MealRepository {
    suspend fun explore(): List<Meal>
}