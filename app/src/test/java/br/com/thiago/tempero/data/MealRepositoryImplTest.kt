package br.com.thiago.tempero.data

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.testing.fake.FakeMealRemoteDataSource
import br.com.thiago.tempero.core.testing.util.assertFails
import br.com.thiago.tempero.core.testing.util.categoryDto
import br.com.thiago.tempero.core.testing.util.mealDto
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class MealRepositoryImplTest {
    private val remote = FakeMealRemoteDataSource()

    private fun TestScope.repository(): MealRepositoryImpl {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        return MealRepositoryImpl(remote, dispatcher, dispatcher)
    }

    @Test
    fun `getAllMeals consulta todas as categorias, junta e ordena por nome`() = runTest {
        remote.byCategory["Beef"] = listOf(mealDto("2", "Banana Pie"), mealDto("1", "Apple Pie"))
        remote.byCategory["Chicken"] = listOf(mealDto("3", "Aioli"))

        val meals = repository().getAllMeals(listOf(MealCategory("Beef"), MealCategory("Chicken")))

        assertEquals(listOf("Aioli", "Apple Pie", "Banana Pie"), meals.map { it.name })
        assertEquals(setOf("Beef", "Chicken"), remote.categoryCalls.toSet())
        // filter.php não manda strCategory; a categoria consultada vira a da receita.
        assertEquals(MealCategory("Chicken"), meals.first { it.id == 3L }.category)
        assertEquals(MealCategory("Beef"), meals.first { it.id == 1L }.category)
    }

    @Test
    fun `getAllMeals sem categorias devolve lista vazia`() = runTest {
        assertEquals(emptyList<Any>(), repository().getAllMeals(emptyList()))
    }

    @Test
    fun `searchMeals faz trim da consulta`() = runTest {
        remote.byName["pasta"] = listOf(mealDto("1", "Pasta", category = "Pasta"))

        val result = repository().searchMeals("  pasta ")

        assertEquals(listOf("pasta"), remote.nameCalls.toList())
        assertEquals(listOf(1L), result.map { it.id })
    }

    @Test
    fun `searchMeals descarta receitas sem categoria`() = runTest {
        remote.byName["pasta"] = listOf(mealDto("1", "Pasta", category = null))

        assertTrue(repository().searchMeals("pasta").isEmpty())
    }

    @Test
    fun `getMealById converte a receita e devolve null quando nao existe`() = runTest {
        remote.byId[5] = mealDto("5", "Pie", category = "Beef", instructions = "Mix.")

        val repo = repository()

        assertEquals("Pie", repo.getMealById(5)?.name)
        assertNull(repo.getMealById(404))
    }

    @Test
    fun `listCategories converte e faz trim`() = runTest {
        remote.categories = listOf(categoryDto(" Beef "), categoryDto("Chicken"))

        assertEquals(
            listOf(MealCategory("Beef"), MealCategory("Chicken")),
            repository().listCategories()
        )
    }

    @Test
    fun `IOException vira Network`() = runTest {
        val cause = IOException("sem rede")
        remote.error = cause

        val error = assertFails<DataError.Network> { repository().listCategories() }

        assertEquals(cause, error.cause)
    }

    @Test
    fun `HttpException vira Unavailable`() = runTest {
        remote.error = HttpException(Response.error<Any>(500, "".toResponseBody()))

        assertFails<DataError.Unavailable> { repository().getMealById(1) }
    }

    @Test
    fun `erros do Moshi viram InvalidResponse`() = runTest {
        remote.error = JsonDataException("tipo errado")
        assertFails<DataError.InvalidResponse> { repository().searchMeals("pasta") }

        remote.error = JsonEncodingException("json quebrado")
        assertFails<DataError.InvalidResponse> { repository().searchMeals("pasta") }
    }

    @Test
    fun `outras excecoes viram Unknown`() = runTest {
        remote.error = IllegalArgumentException("bug")

        assertFails<DataError.Unknown> { repository().listCategories() }
    }

    @Test
    fun `CancellationException e repassada sem conversao`() = runTest {
        remote.error = CancellationException("cancelado")

        assertFails<CancellationException> { repository().listCategories() }
    }

    @Test
    fun `falha em uma categoria derruba o getAllMeals inteiro`() = runTest {
        remote.error = IOException()

        assertFails<DataError.Network> { repository().getAllMeals(listOf(MealCategory("Beef"))) }
    }
}
