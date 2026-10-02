package br.com.thiago.tempero.data.remote

import br.com.thiago.tempero.data.remote.api.TheMealDBAPI
import br.com.thiago.tempero.data.remote.dto.CategoryDto
import br.com.thiago.tempero.data.remote.dto.MealDto
import jakarta.inject.Inject

interface MealRemoteDataSource {
    suspend fun searchMealsByFirstLetter(firstLetter: Char): List<MealDto>

    suspend fun listCategories(): List<CategoryDto>
}

class MealRemoteDataSourceImpl @Inject constructor(
    private val api: TheMealDBAPI
): MealRemoteDataSource {
    override suspend fun searchMealsByFirstLetter(firstLetter: Char): List<MealDto> =
        api.searchByFirstLetter(firstLetter).meals.orEmpty()

    override suspend fun listCategories(): List<CategoryDto> =
        api.listCategories().categories.orEmpty()
}