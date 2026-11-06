package br.com.thiago.tempero.data

import android.database.sqlite.SQLiteException
import br.com.thiago.tempero.data.local.MealLocalDataSource
import br.com.thiago.tempero.data.local.mapper.toDomain
import br.com.thiago.tempero.data.local.mapper.toEntity
import br.com.thiago.tempero.di.DefaultDispatcher
import br.com.thiago.tempero.di.IoDispatcher
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
            .catch { throw it.toStorageError() }

    override fun observeIsFavorite(id: Long): Flow<Boolean> =
        localDataSource.observeIsFavorite(id)
            .catch { throw it.toStorageError() }

    override suspend fun addFavorite(meal: Meal) =
        safeDbCall {
            withContext(ioDispatcher) {
                localDataSource.saveFavorite(meal.toEntity(savedAt = System.currentTimeMillis()))
            }
        }

    override suspend fun removeFavorite(id: Long) =
        safeDbCall {
            withContext(ioDispatcher) {
                localDataSource.deleteFavorite(id)
            }
        }

    internal suspend fun <T> safeDbCall(block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e.toStorageError()
        }

    private fun Throwable.toStorageError(): DataError = when (this) {
        is DataError -> this
        is SQLiteException, is IllegalStateException -> DataError.Storage(this)
        else -> DataError.Unknown(this)
    }
}
