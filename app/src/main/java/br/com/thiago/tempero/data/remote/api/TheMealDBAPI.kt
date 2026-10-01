package br.com.thiago.tempero.data.remote.api

import br.com.thiago.tempero.BuildConfig
import br.com.thiago.tempero.data.remote.dto.CategoriesResponseDto
import br.com.thiago.tempero.data.remote.dto.MealsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDBAPI {
    @GET("search.php")
    suspend fun searchByFirstLetter(@Query("f") letter: Char): MealsResponseDto

    @GET("categories.php")
    suspend fun listCategories(): CategoriesResponseDto
}

const val BASE_URL = "https://www.themealdb.com/api/json/v1/${BuildConfig.MEALDB_API_KEY}"