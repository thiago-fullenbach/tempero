package br.com.thiago.tempero.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMealDao {
    @Query("SELECT * FROM favorite_meals ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<FavoriteMealEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE id = :id)")
    fun observeIsFavorite(id: Long): Flow<Boolean>

    @Upsert
    suspend fun upsert(entity: FavoriteMealEntity)

    @Query("DELETE FROM favorite_meals WHERE id = :id")
    suspend fun deleteById(id: Long)
}
