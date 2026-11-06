package br.com.thiago.tempero.data

import br.com.thiago.tempero.data.remote.MealRemoteDataSource
import br.com.thiago.tempero.data.remote.mapper.toDomain
import br.com.thiago.tempero.di.DefaultDispatcher
import br.com.thiago.tempero.di.IoDispatcher
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.repository.MealRepository
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: MealRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : MealRepository {
    override suspend fun getAllMeals(categories: List<MealCategory>): List<Meal> =
        safeApiCall {
            val dtosByCategory = withContext(ioDispatcher) {
                coroutineScope {
                    categories
                        .map { cat ->
                            async {
                                remoteDataSource.searchMealsByCategory(cat.category).toDomain(cat)
                            }
                        }
                        .awaitAll()
                }
            }

            withContext(defaultDispatcher) {
                coroutineScope {
                    dtosByCategory
                        .flatten()
                        .sortedBy { it.name }
                }
            }
        }

    override suspend fun searchMeals(query: String): List<Meal> =
        safeApiCall {
            withContext(ioDispatcher) {
                remoteDataSource.searchMealsByName(query.trim()).toDomain()
            }
        }

    override suspend fun listCategories(): List<MealCategory> =
        safeApiCall {
            withContext(ioDispatcher) {
                remoteDataSource.listCategories().toDomain()
            }
        }

    internal suspend fun <T> safeApiCall(block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonDataException) {
            throw DataError.InvalidResponse(e)
        } catch (e: JsonEncodingException) {
            throw DataError.InvalidResponse(e)
        } catch (e: IOException) {
            throw DataError.Network(e)
        } catch (e: HttpException) {
            throw DataError.Unavailable(e)
        } catch (e: Exception) {
            throw DataError.Unknown(e)
        }
}