package br.com.thiago.tempero.core.data.remote

import br.com.thiago.tempero.core.data.MealRepositoryImpl
import br.com.thiago.tempero.core.network.MealRemoteDataSourceImpl
import br.com.thiago.tempero.core.network.api.TheMealDBAPI
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.model.MealIngredient
import br.com.thiago.tempero.core.testing.util.assertFails
import br.com.thiago.tempero.core.testing.util.readFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Collections

/**
 * Integração Retrofit + Moshi + data source + repositório contra um servidor HTTP falso,
 * a mesma pilha que o [br.com.thiago.tempero.core.network.di.ApiModule] monta em produção.
 */
class MealRemoteIntegrationTest {
    private val server = MockWebServer()
    private val requests: MutableList<String> = Collections.synchronizedList(mutableListOf())
    private lateinit var repository: MealRepositoryImpl

    @Before
    fun setUp() {
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/api/json/v1/1/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TheMealDBAPI::class.java)
        repository = MealRepositoryImpl(MealRemoteDataSourceImpl(api), Dispatchers.IO, Dispatchers.Default)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    /** Responde por caminho (sem o prefixo da chave da API); o que não estiver no mapa devolve 404. */
    private fun respond(vararg routes: Pair<String, MockResponse>) {
        val byPath = routes.toMap()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty().removePrefix("/api/json/v1/1/")
                requests += path
                return byPath[path] ?: MockResponse().setResponseCode(404)
            }
        }
    }

    private fun json(fixture: String) = MockResponse().setBody(readFixture(fixture))

    @Test
    fun `listCategories le categories php`() = runBlocking<Unit> {
        respond("categories.php" to json("categories.json"))

        assertEquals(listOf(MealCategory("Beef"), MealCategory("Chicken")), repository.listCategories())
        assertEquals(listOf("categories.php"), requests.toList())
    }

    @Test
    fun `getAllMeals consulta cada categoria, junta e ordena por nome`() = runBlocking<Unit> {
        respond(
            "filter.php?c=Beef" to json("filter_beef.json"),
            "filter.php?c=Chicken" to json("filter_chicken.json")
        )

        val meals = repository.getAllMeals(listOf(MealCategory("Beef"), MealCategory("Chicken")))

        // Ordem por nome, com maiúscula antes de minúscula: "Beef W…" < "Beef a…" < "Chicken…".
        assertEquals(
            listOf("Beef Wellington", "Beef and Mustard Pie", "Chicken Alfredo Primavera"),
            meals.map { it.name }
        )
        assertEquals(
            listOf("Beef", "Beef", "Chicken"),
            meals.map { it.category.category }
        )
        assertEquals(setOf("filter.php?c=Beef", "filter.php?c=Chicken"), requests.toSet())
    }

    @Test
    fun `getMealById converte lookup php em receita completa`() = runBlocking<Unit> {
        respond("lookup.php?i=52874" to json("lookup_52874.json"))

        val meal = repository.getMealById(52874)!!

        assertEquals("Beef and Mustard Pie", meal.name)
        assertEquals(MealCategory("Beef"), meal.category)
        assertEquals("British", meal.area)
        assertEquals(listOf("Meat", "Pie"), meal.tags)
        // Ingredientes vazios, em branco e nulos do JSON são ignorados.
        assertEquals(
            listOf(
                MealIngredient("Beef", "1kg"),
                MealIngredient("Plain Flour", "2 tbs"),
                MealIngredient("Rapeseed Oil", "2 tbs")
            ),
            meal.ingredients
        )
        // Três linhas no texto original, cada uma vira um passo (linhas nunca se misturam).
        assertEquals(3, meal.steps.size)
        assertTrue(meal.steps.first().step.startsWith("Preheat the oven"))
    }

    @Test
    fun `searchMeals faz trim e manda a consulta em search php`() = runBlocking<Unit> {
        respond("search.php?s=beef" to json("lookup_52874.json"))

        val result = repository.searchMeals("  beef ")

        assertEquals(listOf("search.php?s=beef"), requests.toList())
        assertEquals(listOf(52874L), result.map { it.id })
    }

    @Test
    fun `meals null significa lista vazia ou receita inexistente`() = runBlocking<Unit> {
        respond(
            "lookup.php?i=1" to json("meals_null.json"),
            "search.php?s=zzz" to json("meals_null.json"),
            "filter.php?c=Beef" to json("meals_null.json")
        )

        assertNull(repository.getMealById(1))
        assertTrue(repository.searchMeals("zzz").isEmpty())
        assertTrue(repository.getAllMeals(listOf(MealCategory("Beef"))).isEmpty())
    }

    @Test
    fun `HTTP 500 vira Unavailable`() = runBlocking<Unit> {
        respond("categories.php" to MockResponse().setResponseCode(500))

        assertFails<DataError.Unavailable> { repository.listCategories() }
    }

    @Test
    fun `corpo que nao e JSON vira InvalidResponse`() = runBlocking<Unit> {
        respond("categories.php" to json("malformed.json"))

        assertFails<DataError.InvalidResponse> { repository.listCategories() }
    }

    @Test
    fun `tipo errado no JSON vira InvalidResponse`() = runBlocking<Unit> {
        respond("search.php?s=beef" to json("wrong_type.json"))

        assertFails<DataError.InvalidResponse> { repository.searchMeals("beef") }
    }

    @Test
    fun `conexao derrubada vira Network`() = runBlocking<Unit> {
        respond("categories.php" to MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        assertFails<DataError.Network> { repository.listCategories() }
    }

    @Test
    fun `servidor fora do ar vira Network`() = runBlocking<Unit> {
        server.shutdown()

        assertFails<DataError.Network> { repository.listCategories() }
    }
}
