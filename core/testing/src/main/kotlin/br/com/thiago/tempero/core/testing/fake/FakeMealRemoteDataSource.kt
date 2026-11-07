package br.com.thiago.tempero.core.testing.fake

import br.com.thiago.tempero.core.network.MealRemoteDataSource
import br.com.thiago.tempero.core.network.dto.CategoryDto
import br.com.thiago.tempero.core.network.dto.MealDto
import java.util.concurrent.CopyOnWriteArrayList

class FakeMealRemoteDataSource : MealRemoteDataSource {
    var categories: List<CategoryDto> = emptyList()
    val byCategory = mutableMapOf<String, List<MealDto>>()
    val byName = mutableMapOf<String, List<MealDto>>()
    val byId = mutableMapOf<Long, MealDto>()

    /** Se definido, toda chamada lança este erro. */
    @Volatile
    var error: Throwable? = null

    val categoryCalls = CopyOnWriteArrayList<String>()
    val nameCalls = CopyOnWriteArrayList<String>()
    val idCalls = CopyOnWriteArrayList<Long>()

    override suspend fun searchMealsByCategory(category: String): List<MealDto> {
        categoryCalls += category
        error?.let { throw it }
        return byCategory[category].orEmpty()
    }

    override suspend fun searchMealsByName(name: String): List<MealDto> {
        nameCalls += name
        error?.let { throw it }
        return byName[name].orEmpty()
    }

    override suspend fun getMealById(id: Long): MealDto? {
        idCalls += id
        error?.let { throw it }
        return byId[id]
    }

    override suspend fun listCategories(): List<CategoryDto> {
        error?.let { throw it }
        return categories
    }
}
