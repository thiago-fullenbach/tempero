package br.com.thiago.tempero.core.domain.usecase

import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.testing.fake.FakeFavoriteRepository
import br.com.thiago.tempero.core.testing.fake.FakeMealRepository
import br.com.thiago.tempero.core.testing.util.assertFails
import br.com.thiago.tempero.core.testing.util.completeMeal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMealDetailUseCaseTest {
    private val favorites = FakeFavoriteRepository()
    private val meals = FakeMealRepository()
    private val getDetail = GetMealDetailUseCase(favorites, meals)

    @Test
    fun `prioriza o favorito salvo e nao chama a API`() = runTest {
        val saved = completeMeal(id = 1, name = "Salva")
        favorites.addFavorite(saved)
        meals.byId[1] = completeMeal(id = 1, name = "Da API")

        assertEquals(saved, getDetail(1))
        assertTrue(meals.getByIdCalls.isEmpty())
    }

    @Test
    fun `sem favorito busca na API`() = runTest {
        val remote = completeMeal(id = 1, name = "Da API")
        meals.byId[1] = remote

        assertEquals(remote, getDetail(1))
        assertEquals(listOf(1L), meals.getByIdCalls)
    }

    @Test
    fun `sem favorito e sem API devolve null`() = runTest {
        assertNull(getDetail(1))
    }

    @Test
    fun `erro da API e repassado`() = runTest {
        meals.error = DataError.Unavailable(Exception())

        assertFails<DataError.Unavailable> { getDetail(1) }
    }

    @Test
    fun `erro no Room e repassado sem tentar a API`() = runTest {
        favorites.writeError = DataError.Storage(IllegalStateException())

        assertFails<DataError.Storage> { getDetail(1) }
        assertTrue(meals.getByIdCalls.isEmpty())
    }
}
