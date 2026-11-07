package br.com.thiago.tempero.domain.usecase

import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.MealStep
import br.com.thiago.tempero.fake.FakeFavoriteRepository
import br.com.thiago.tempero.fake.FakeMealRepository
import br.com.thiago.tempero.testutil.assertFails
import br.com.thiago.tempero.testutil.completeMeal
import br.com.thiago.tempero.testutil.meal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleFavoriteUseCaseTest {
    private val favorites = FakeFavoriteRepository()
    private val meals = FakeMealRepository()
    private val toggle = ToggleFavoriteUseCase(favorites, meals)

    @Test
    fun `receita ja favorita e removida e retorna false`() = runTest {
        val meal = completeMeal(id = 1)
        favorites.addFavorite(meal)

        val result = toggle(meal)

        assertFalse(result)
        assertTrue(favorites.store.value.isEmpty())
        assertTrue(meals.getByIdCalls.isEmpty())
    }

    @Test
    fun `receita completa e salva sem consultar a API`() = runTest {
        val meal = completeMeal(id = 1)

        val result = toggle(meal)

        assertTrue(result)
        assertEquals(meal, favorites.store.value[1])
        assertTrue(meals.getByIdCalls.isEmpty())
    }

    @Test
    fun `receita so com preparo conta como completa`() = runTest {
        val meal = meal(id = 1, steps = listOf("Mix."))

        toggle(meal)

        assertEquals(listOf(MealStep("Mix.")), favorites.store.value.getValue(1).steps)
        assertTrue(meals.getByIdCalls.isEmpty())
    }

    @Test
    fun `receita incompleta do filter busca a completa na API antes de salvar`() = runTest {
        val incomplete = meal(id = 5, name = "Pie")
        val complete = completeMeal(id = 5, name = "Pie")
        meals.byId[5] = complete

        val result = toggle(incomplete)

        assertTrue(result)
        assertEquals(listOf(5L), meals.getByIdCalls)
        assertEquals(complete, favorites.store.value[5])
    }

    @Test
    fun `API sem a receita lanca InvalidResponse e nao salva nada`() = runTest {
        assertFails<DataError.InvalidResponse> { toggle(meal(id = 5)) }

        assertTrue(favorites.store.value.isEmpty())
    }

    @Test
    fun `erro da API ao completar e repassado`() = runTest {
        meals.error = DataError.Network(java.io.IOException())

        assertFails<DataError.Network> { toggle(meal(id = 5)) }
    }

    @Test
    fun `alternar duas vezes volta ao estado inicial`() = runTest {
        val meal = completeMeal(id = 1)

        assertTrue(toggle(meal))
        assertFalse(toggle(meal))
        assertTrue(favorites.store.value.isEmpty())
    }
}
