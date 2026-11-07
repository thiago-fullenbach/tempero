package br.com.thiago.tempero.core.domain.repository

import br.com.thiago.tempero.core.model.Meal
import kotlinx.coroutines.flow.Flow

/** Receitas favoritas, guardadas localmente (funcionam sem internet). */
interface FavoriteRepository {
    /**
     * Favoritos ordenados por nome. Falhas do banco chegam como
     * [br.com.thiago.tempero.core.model.DataError.Storage].
     */
    fun observeFavorites(): Flow<List<Meal>>

    fun observeIsFavorite(id: Long): Flow<Boolean>

    /** Receita favorita completa, salva localmente; `null` se não está nos favoritos. */
    suspend fun getFavorite(id: Long): Meal?

    suspend fun addFavorite(meal: Meal)

    suspend fun removeFavorite(id: Long)
}
