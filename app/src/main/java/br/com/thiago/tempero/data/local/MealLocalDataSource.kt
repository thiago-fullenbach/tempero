package br.com.thiago.tempero.data.local

import br.com.thiago.tempero.data.local.dao.FavoriteMealDao
import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

interface MealLocalDataSource {
    fun observeFavorites(): Flow<List<FavoriteMealEntity>>

    fun observeIsFavorite(id: Long): Flow<Boolean>

    suspend fun saveFavorite(entity: FavoriteMealEntity)

    suspend fun deleteFavorite(id: Long)
}

class MealLocalDataSourceImpl @Inject constructor(
    private val dao: FavoriteMealDao
) : MealLocalDataSource {
    override fun observeFavorites(): Flow<List<FavoriteMealEntity>> = dao.observeAll()

    override fun observeIsFavorite(id: Long): Flow<Boolean> = dao.observeIsFavorite(id)

    override suspend fun saveFavorite(entity: FavoriteMealEntity) = dao.upsert(entity)

    override suspend fun deleteFavorite(id: Long) = dao.deleteById(id)
}
