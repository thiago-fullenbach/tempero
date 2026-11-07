package br.com.thiago.tempero.presentation.paging

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.testutil.meal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MealPagingSourceTest {
    private val meals = (1L..5L).map { meal(id = it, category = if (it % 2L == 0L) "Chicken" else "Beef") }

    private suspend fun MealPagingSource.load(key: Int?, size: Int): LoadResult.Page<Int, Meal> {
        val params: LoadParams<Int> = if (key == null) LoadParams.Refresh(key = null, loadSize = size, placeholdersEnabled = false)
        else LoadParams.Append(key = key, loadSize = size, placeholdersEnabled = false)
        return load(params) as LoadResult.Page<Int, Meal>
    }

    @Test
    fun `primeira pagina traz a fatia inicial e a proxima chave`() = runTest {
        val page = MealPagingSource(meals = meals).load(null, 2)

        assertEquals(listOf(1L, 2L), page.data.map { it.id })
        assertEquals(2, page.nextKey)
        assertNull(page.prevKey)
    }

    @Test
    fun `pagina seguinte continua de onde parou`() = runTest {
        val page = MealPagingSource(meals = meals).load(2, 2)

        assertEquals(listOf(3L, 4L), page.data.map { it.id })
        assertEquals(4, page.nextKey)
    }

    @Test
    fun `ultima pagina nao tem proxima chave`() = runTest {
        val page = MealPagingSource(meals = meals).load(4, 2)

        assertEquals(listOf(5L), page.data.map { it.id })
        assertNull(page.nextKey)
    }

    @Test
    fun `pagina que termina exatamente no fim nao tem proxima chave`() = runTest {
        val page = MealPagingSource(meals = meals).load(null, 5)

        assertEquals(5, page.data.size)
        assertNull(page.nextKey)
    }

    @Test
    fun `chave alem do fim devolve pagina vazia`() = runTest {
        val page = MealPagingSource(meals = meals).load(10, 2)

        assertEquals(emptyList<Meal>(), page.data)
        assertNull(page.nextKey)
    }

    @Test
    fun `filtra pela categoria selecionada`() = runTest {
        val page = MealPagingSource(selectedCategory = MealCategory("Beef"), meals = meals).load(null, 10)

        assertEquals(listOf(1L, 3L, 5L), page.data.map { it.id })
    }

    @Test
    fun `categoria sem receitas devolve vazio`() = runTest {
        val page = MealPagingSource(selectedCategory = MealCategory("Dessert"), meals = meals).load(null, 10)

        assertEquals(emptyList<Meal>(), page.data)
        assertNull(page.nextKey)
    }
}
