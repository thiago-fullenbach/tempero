package br.com.thiago.tempero.fake

import br.com.thiago.tempero.data.local.MealLocalDataSource
import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeMealLocalDataSource : MealLocalDataSource {
    val store = MutableStateFlow<Map<Long, FavoriteMealEntity>>(emptyMap())

    /** Erro lançado por todas as operações (suspend e Flows). */
    var error: Throwable? = null

    override fun observeFavorites(): Flow<List<FavoriteMealEntity>> =
        store.map { map ->
            error?.let { throw it }
            map.values.sortedBy { it.name.lowercase() }
        }

    override fun observeIsFavorite(id: Long): Flow<Boolean> =
        store.map {
            error?.let { throw it }
            id in it
        }

    override suspend fun getFavorite(id: Long): FavoriteMealEntity? {
        error?.let { throw it }
        return store.value[id]
    }

    override suspend fun saveFavorite(entity: FavoriteMealEntity) {
        error?.let { throw it }
        store.value = store.value + (entity.id to entity)
    }

    override suspend fun deleteFavorite(id: Long) {
        error?.let { throw it }
        store.value = store.value - id
    }
}
