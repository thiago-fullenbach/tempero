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
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.measureTimedValue

class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: MealRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : MealRepository {
    override suspend fun getAllMeals(categories: List<MealCategory>): List<Meal> =
        safeApiCall("getAllMeals(${categories.size} categorias)") {
            val (meals, elapsed) = measureTimedValue {
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
            Timber.d(
                "getAllMeals: %d receitas de %d categorias em %d ms",
                meals.size, categories.size, elapsed.inWholeMilliseconds
            )
            meals
        }

    override suspend fun searchMeals(query: String): List<Meal> =
        safeApiCall("searchMeals(q=$query)") {
            withContext(ioDispatcher) {
                remoteDataSource.searchMealsByName(query.trim()).toDomain()
            }
        }

    override suspend fun getMealById(id: Long): Meal? =
        safeApiCall("getMealById($id)") {
            val meal = withContext(ioDispatcher) {
                remoteDataSource.getMealById(id)?.toDomain()
            }
            if (meal == null) Timber.w("getMealById: receita %d não encontrada na API", id)
            meal
        }

    override suspend fun listCategories(): List<MealCategory> =
        safeApiCall("listCategories") {
            withContext(ioDispatcher) {
                remoteDataSource.listCategories().toDomain()
            }
        }

    internal suspend fun <T> safeApiCall(operation: String, block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonDataException) {
            Timber.e(e, "Resposta inválida em %s", operation)
            throw DataError.InvalidResponse(e)
        } catch (e: JsonEncodingException) {
            Timber.e(e, "Resposta inválida em %s", operation)
            throw DataError.InvalidResponse(e)
        } catch (e: IOException) {
            Timber.w(e, "Falha de rede em %s", operation)
            throw DataError.Network(e)
        } catch (e: HttpException) {
            Timber.w(e, "HTTP %d em %s", e.code(), operation)
            throw DataError.Unavailable(e)
        } catch (e: Exception) {
            Timber.e(e, "Erro inesperado em %s", operation)
            throw DataError.Unknown(e)
        }
}
