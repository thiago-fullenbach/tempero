package br.com.thiago.tempero.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import br.com.thiago.tempero.data.local.MealLocalDataSourceImpl
import br.com.thiago.tempero.data.local.TemperoDatabase
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.testutil.assertFails
import br.com.thiago.tempero.testutil.completeMeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.seconds

/** `FavoriteRepositoryImpl` + `MealLocalDataSourceImpl` + DAO real sobre Room em memória. */
@RunWith(RobolectricTestRunner::class)
class FavoriteRepositoryIntegrationTest {
    private lateinit var db: TemperoDatabase
    private lateinit var repository: FavoriteRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TemperoDatabase::class.java).build()
        repository = FavoriteRepositoryImpl(
            localDataSource = MealLocalDataSourceImpl(db.favoriteMealDao()),
            ioDispatcher = Dispatchers.IO,
            defaultDispatcher = Dispatchers.Default
        )
    }

    @After
    fun tearDown() {
        if (db.isOpen) db.close()
    }

    @Test
    fun `salvar, ler e remover de ponta a ponta`() = runBlocking<Unit> {
        val meal = completeMeal(id = 1, name = "Pie")

        repository.addFavorite(meal)
        assertEquals(meal, repository.getFavorite(1))

        repository.removeFavorite(1)
        assertNull(repository.getFavorite(1))
    }

    @Test
    fun `observeFavorites emite a lista a cada mudanca`() = runBlocking<Unit> {
        repository.observeFavorites().test(timeout = 5.seconds) {
            assertEquals(emptyList<Any>(), awaitItem())

            repository.addFavorite(completeMeal(id = 2, name = "banana"))
            assertEquals(listOf("banana"), awaitItem().map { it.name })

            repository.addFavorite(completeMeal(id = 1, name = "Apple"))
            assertEquals(listOf("Apple", "banana"), awaitItem().map { it.name })

            repository.removeFavorite(2)
            assertEquals(listOf("Apple"), awaitItem().map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeIsFavorite acompanha o banco`() = runBlocking<Unit> {
        repository.observeIsFavorite(1).test(timeout = 5.seconds) {
            assertEquals(false, awaitItem())
            repository.addFavorite(completeMeal(id = 1))
            assertEquals(true, awaitItem())
            repository.removeFavorite(1)
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `adicionar duas vezes mantem uma unica linha`() = runBlocking<Unit> {
        repository.addFavorite(completeMeal(id = 1, name = "Pie"))
        repository.addFavorite(completeMeal(id = 1, name = "Pie v2"))

        repository.observeFavorites().test(timeout = 5.seconds) {
            assertEquals(listOf("Pie v2"), awaitItem().map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SQLiteException do Room vira DataError Storage`() = runBlocking<Unit> {
        // Tabela sumindo faz o Room lançar SQLiteException ("no such table"). Fechar o banco não serve:
        // o Room cancela o próprio escopo e a CancellationException é (corretamente) repassada.
        db.openHelper.writableDatabase.execSQL("DROP TABLE favorite_meals")

        assertFails<DataError.Storage> { repository.getFavorite(1) }
        assertFails<DataError.Storage> { repository.addFavorite(completeMeal(id = 1)) }
    }
}
