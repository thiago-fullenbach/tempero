package br.com.thiago.tempero.data

import br.com.thiago.tempero.data.local.mapper.toEntity
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.testing.fake.FakeMealLocalDataSource
import br.com.thiago.tempero.core.testing.util.assertFails
import br.com.thiago.tempero.core.testing.util.completeMeal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImplTest {
    private val local = FakeMealLocalDataSource()

    private fun TestScope.repository(): FavoriteRepositoryImpl {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        return FavoriteRepositoryImpl(local, dispatcher, dispatcher)
    }

    @Test
    fun `addFavorite salva a entidade e getFavorite devolve a receita`() = runTest {
        val repo = repository()
        val meal = completeMeal(id = 1, name = "Pie")

        repo.addFavorite(meal)

        assertTrue(local.store.value.getValue(1).savedAt > 0)
        assertEquals(meal, repo.getFavorite(1))
    }

    @Test
    fun `getFavorite devolve null para receita que nao e favorita`() = runTest {
        assertNull(repository().getFavorite(99))
    }

    @Test
    fun `removeFavorite apaga a receita`() = runTest {
        val repo = repository()
        repo.addFavorite(completeMeal(id = 1))

        repo.removeFavorite(1)

        assertNull(repo.getFavorite(1))
    }

    @Test
    fun `observeFavorites converte as entidades em receitas`() = runTest {
        local.store.value = mapOf(
            2L to completeMeal(id = 2, name = "b").toEntity(0),
            1L to completeMeal(id = 1, name = "A").toEntity(0)
        )

        val favorites = repository().observeFavorites().first()

        assertEquals(listOf("A", "b"), favorites.map { it.name })
    }

    @Test
    fun `observeIsFavorite acompanha o armazenamento`() = runTest {
        val repo = repository()

        assertFalse(repo.observeIsFavorite(1).first())
        repo.addFavorite(completeMeal(id = 1))
        assertTrue(repo.observeIsFavorite(1).first())
    }

    @Test
    fun `IllegalStateException vira Storage`() = runTest {
        local.error = IllegalStateException("banco fechado")

        assertFails<DataError.Storage> { repository().getFavorite(1) }
        assertFails<DataError.Storage> { repository().removeFavorite(1) }
    }

    @Test
    fun `outras excecoes viram Unknown`() = runTest {
        local.error = IOException("disco")

        assertFails<DataError.Unknown> { repository().addFavorite(completeMeal()) }
    }

    @Test
    fun `DataError e repassado sem conversao`() = runTest {
        val original = DataError.Network(IOException())
        local.error = original

        assertSame(original, assertFails<DataError.Network> { repository().getFavorite(1) })
    }

    @Test
    fun `CancellationException e repassada`() = runTest {
        local.error = CancellationException("cancelado")

        assertFails<CancellationException> { repository().getFavorite(1) }
    }

    @Test
    fun `falha nos Flows chega como DataError`() = runTest {
        local.error = IllegalStateException("banco fechado")
        val repo = repository()

        assertFails<DataError.Storage> { repo.observeFavorites().first() }
        assertFails<DataError.Storage> { repo.observeIsFavorite(1).first() }
    }
}
