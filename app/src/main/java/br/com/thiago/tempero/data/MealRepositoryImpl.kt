package br.com.thiago.tempero.data

import br.com.thiago.tempero.data.remote.MealRemoteDataSource
import br.com.thiago.tempero.data.remote.mapper.toDomain
import br.com.thiago.tempero.domain.error.AppError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.repository.MealRepository
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class MealRepositoryImpl(
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
            throw AppError.InvalidData(e)
        } catch (e: JsonEncodingException) {
            throw AppError.InvalidData(e)
        } catch (e: IOException) {
            throw AppError.NoConnection(e)
        } catch (e: HttpException) {
            throw AppError.Server(e.code())
        } catch (e: Exception) {
            throw AppError.Unknown(e)
        }
}