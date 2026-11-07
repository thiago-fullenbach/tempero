package br.com.thiago.tempero.core.data

import android.database.sqlite.SQLiteException
import br.com.thiago.tempero.core.database.MealLocalDataSource
import br.com.thiago.tempero.core.data.local.mapper.toDomain
import br.com.thiago.tempero.core.data.local.mapper.toEntity
import br.com.thiago.tempero.core.common.DefaultDispatcher
import br.com.thiago.tempero.core.common.IoDispatcher
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.domain.repository.FavoriteRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class FavoriteRepositoryImpl @Inject constructor(
    private val localDataSource: MealLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : FavoriteRepository {
    override fun observeFavorites(): Flow<List<Meal>> =
        localDataSource.observeFavorites()
            .map { it.toDomain() }
            .flowOn(defaultDispatcher)
            .catch { throw it.toStorageError("observeFavorites") }

    override fun observeIsFavorite(id: Long): Flow<Boolean> =
        localDataSource.observeIsFavorite(id)
            .catch { throw it.toStorageError("observeIsFavorite($id)") }

    override suspend fun getFavorite(id: Long): Meal? =
        safeDbCall("getFavorite($id)") {
            val meal = withContext(ioDispatcher) {
                localDataSource.getFavorite(id)?.toDomain()
            }
            Timber.d("getFavorite: id=%d %s", id, if (meal != null) "encontrado no Room" else "não está no Room")
            meal
        }

    override suspend fun addFavorite(meal: Meal) =
        safeDbCall("addFavorite(${meal.id})") {
            withContext(ioDispatcher) {
                localDataSource.saveFavorite(meal.toEntity(savedAt = System.currentTimeMillis()))
            }
            Timber.d("Favorito salvo: id=%d", meal.id)
        }

    override suspend fun removeFavorite(id: Long) =
        safeDbCall("removeFavorite($id)") {
            withContext(ioDispatcher) {
                localDataSource.deleteFavorite(id)
            }
            Timber.d("Favorito removido: id=%d", id)
        }

    internal suspend fun <T> safeDbCall(operation: String, block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e.toStorageError(operation)
        }

    private fun Throwable.toStorageError(operation: String): DataError = when (this) {
        is DataError -> this
        is SQLiteException, is IllegalStateException -> {
            Timber.e(this, "Falha no Room em %s", operation)
            DataError.Storage(this)
        }
        else -> {
            Timber.e(this, "Erro inesperado no Room em %s", operation)
            DataError.Unknown(this)
        }
    }
}
