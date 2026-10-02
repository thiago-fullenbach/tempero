package br.com.thiago.tempero.data

import br.com.thiago.tempero.data.remote.MealRemoteDataSource
import br.com.thiago.tempero.data.remote.mapper.toDomain
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.repository.MealRepository
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import jakarta.inject.Inject
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: MealRemoteDataSource
): MealRepository {
    override suspend fun searchMealsByFirstLetter(firstLetter: Char): List<Meal> =
        safeApiCall { remoteDataSource.searchMealsByFirstLetter(firstLetter).toDomain() }

    override suspend fun listCategories(): List<MealCategory> =
        safeApiCall { remoteDataSource.listCategories().toDomain() }

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