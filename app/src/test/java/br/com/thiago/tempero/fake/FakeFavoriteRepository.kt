package br.com.thiago.tempero.fake

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteRepository(initial: List<Meal> = emptyList()) : FavoriteRepository {
    val store = MutableStateFlow(initial.associateBy { it.id })

    /** Erro lançado pelas operações suspend (get/add/remove). */
    var writeError: Throwable? = null

    /** Se definido, os Flows de observação falham com este erro. */
    var observeError: Throwable? = null

    override fun observeFavorites(): Flow<List<Meal>> =
        store.map { map ->
            observeError?.let { throw it }
            map.values.sortedBy { it.name.lowercase() }
        }

    override fun observeIsFavorite(id: Long): Flow<Boolean> =
        store.map {
            observeError?.let { throw it }
            id in it
        }

    override suspend fun getFavorite(id: Long): Meal? {
        writeError?.let { throw it }
        return store.value[id]
    }

    override suspend fun addFavorite(meal: Meal) {
        writeError?.let { throw it }
        store.value = store.value + (meal.id to meal)
    }

    override suspend fun removeFavorite(id: Long) {
        writeError?.let { throw it }
        store.value = store.value - id
    }
}
