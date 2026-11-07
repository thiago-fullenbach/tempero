package br.com.thiago.tempero.core.network.api

import br.com.thiago.tempero.core.network.BuildConfig
import br.com.thiago.tempero.core.network.dto.CategoriesResponseDto
import br.com.thiago.tempero.core.network.dto.MealsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDBAPI {
    @GET("filter.php")
    suspend fun searchByCategory(@Query("c") category: String?): MealsResponseDto

    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): MealsResponseDto

    @GET("lookup.php")
    suspend fun lookupById(@Query("i") id: Long): MealsResponseDto

    @GET("categories.php")
    suspend fun listCategories(): CategoriesResponseDto
}

const val THE_MEAL_DB_API_BASE_URL = "https://www.themealdb.com/api/json/v1/${BuildConfig.MEALDB_API_KEY}/"