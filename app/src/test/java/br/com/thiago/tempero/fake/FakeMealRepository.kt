package br.com.thiago.tempero.fake

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.repository.MealRepository

class FakeMealRepository : MealRepository {
    var categories: List<MealCategory> = emptyList()
    var meals: List<Meal> = emptyList()
    var searchResults: List<Meal> = emptyList()
    var byId: MutableMap<Long, Meal> = mutableMapOf()

    /** Se definido, toda chamada lança este erro. */
    var error: Throwable? = null

    var listCategoriesCalls = 0
    var getAllMealsCalls = 0
    val searchQueries = mutableListOf<String>()
    val getByIdCalls = mutableListOf<Long>()

    override suspend fun getAllMeals(categories: List<MealCategory>): List<Meal> {
        getAllMealsCalls++
        error?.let { throw it }
        return meals
    }

    override suspend fun searchMeals(query: String): List<Meal> {
        searchQueries += query
        error?.let { throw it }
        return searchResults
    }

    override suspend fun getMealById(id: Long): Meal? {
        getByIdCalls += id
        error?.let { throw it }
        return byId[id]
    }

    override suspend fun listCategories(): List<MealCategory> {
        listCategoriesCalls++
        error?.let { throw it }
        return categories
    }
}
