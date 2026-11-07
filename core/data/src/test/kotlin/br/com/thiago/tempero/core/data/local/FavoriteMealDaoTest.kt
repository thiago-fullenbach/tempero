package br.com.thiago.tempero.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import br.com.thiago.tempero.core.database.TemperoDatabase
import br.com.thiago.tempero.core.database.dao.FavoriteMealDao
import br.com.thiago.tempero.core.data.local.mapper.toEntity
import br.com.thiago.tempero.core.model.MealIngredient
import br.com.thiago.tempero.core.testing.util.completeMeal
import br.com.thiago.tempero.core.testing.util.meal
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class FavoriteMealDaoTest {
    private lateinit var db: TemperoDatabase
    private lateinit var dao: FavoriteMealDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TemperoDatabase::class.java).build()
        dao = db.favoriteMealDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `observeAll ordena por nome ignorando maiusculas`() = runBlocking<Unit> {
        dao.upsert(completeMeal(1, "banana").toEntity(0))
        dao.upsert(completeMeal(2, "Apple").toEntity(0))
        dao.upsert(completeMeal(3, "cherry").toEntity(0))

        dao.observeAll().test(timeout = 5.seconds) {
            assertEquals(listOf("Apple", "banana", "cherry"), awaitItem().map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `upsert com o mesmo id substitui a linha`() = runBlocking<Unit> {
        dao.upsert(completeMeal(1, "Old").toEntity(1))
        dao.upsert(completeMeal(1, "New").toEntity(2))

        dao.observeAll().test(timeout = 5.seconds) {
            val rows = awaitItem()
            assertEquals(1, rows.size)
            assertEquals("New", rows.single().name)
            assertEquals(2L, rows.single().savedAt)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteById remove so a receita pedida`() = runBlocking<Unit> {
        dao.upsert(completeMeal(1, "A").toEntity(0))
        dao.upsert(completeMeal(2, "B").toEntity(0))

        dao.deleteById(1)

        assertNull(dao.getById(1))
        assertEquals("B", dao.getById(2)?.name)
    }

    @Test
    fun `deleteById de id inexistente nao falha`() = runBlocking<Unit> {
        dao.deleteById(404)
        assertNull(dao.getById(404))
    }

    @Test
    fun `observeIsFavorite acompanha inserir e remover`() = runBlocking<Unit> {
        dao.observeIsFavorite(1).test(timeout = 5.seconds) {
            assertEquals(false, awaitItem())

            dao.upsert(completeMeal(1).toEntity(0))
            assertEquals(true, awaitItem())

            dao.deleteById(1)
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `colunas de lista sobrevivem a ida e volta pelo banco`() = runBlocking<Unit> {
        val entity = meal(
            id = 9,
            name = "Pie",
            steps = listOf("Mix, then \"stir\".", "Bake."),
            ingredients = listOf(MealIngredient("Flour", "200g"), MealIngredient("Egg", null)),
            tags = listOf("Meat", "Pie")
        ).toEntity(savedAt = 55)

        dao.upsert(entity)

        assertEquals(entity, dao.getById(9))
    }
}
