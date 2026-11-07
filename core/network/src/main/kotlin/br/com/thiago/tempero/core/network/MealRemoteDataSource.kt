package br.com.thiago.tempero.core.network

import br.com.thiago.tempero.core.network.api.TheMealDBAPI
import br.com.thiago.tempero.core.network.dto.CategoryDto
import br.com.thiago.tempero.core.network.dto.MealDto
import jakarta.inject.Inject

interface MealRemoteDataSource {
    suspend fun searchMealsByCategory(category: String): List<MealDto>

    suspend fun searchMealsByName(name: String): List<MealDto>

    suspend fun getMealById(id: Long): MealDto?

    suspend fun listCategories(): List<CategoryDto>
}

class MealRemoteDataSourceImpl @Inject constructor(
    private val api: TheMealDBAPI
): MealRemoteDataSource {
    override suspend fun searchMealsByCategory(category: String): List<MealDto> =
        api.searchByCategory(category).meals.orEmpty()

    override suspend fun searchMealsByName(name: String): List<MealDto> =
        api.searchByName(name).meals.orEmpty()

    override suspend fun getMealById(id: Long): MealDto? =
        api.lookupById(id).meals?.firstOrNull()

    override suspend fun listCategories(): List<CategoryDto> =
        api.listCategories().categories.orEmpty()
}
