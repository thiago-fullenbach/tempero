package br.com.thiago.tempero.integration

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.thiago.tempero.core.data.FavoriteRepositoryImpl
import br.com.thiago.tempero.core.data.MealRepositoryImpl
import br.com.thiago.tempero.core.database.MealLocalDataSourceImpl
import br.com.thiago.tempero.core.database.TemperoDatabase
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.domain.usecase.GetMealDetailUseCase
import br.com.thiago.tempero.core.domain.usecase.ToggleFavoriteUseCase
import br.com.thiago.tempero.core.testing.fake.FakeAnalyticsTracker
import br.com.thiago.tempero.core.testing.fake.FakeConnectivityObserver
import br.com.thiago.tempero.core.testing.fake.FakeMealRemoteDataSource
import br.com.thiago.tempero.presentation.screen.detail.DetailIntent
import br.com.thiago.tempero.presentation.screen.detail.DetailUiState
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesIntent
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesUiState
import br.com.thiago.tempero.presentation.viewmodel.DetailViewModel
import br.com.thiago.tempero.presentation.viewmodel.FavoritesViewModel
import br.com.thiago.tempero.core.testing.util.MainDispatcherRule
import br.com.thiago.tempero.core.testing.util.awaitValue
import br.com.thiago.tempero.core.testing.util.mealDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * A jornada de favoritos sem UI, com a cadeia real de produção:
 * ViewModels → use cases → repositórios → Room em memória, e API falsa só na ponta remota.
 */
@RunWith(RobolectricTestRunner::class)
class FavoriteFlowIntegrationTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val mealId = 52874L
    private val remote = FakeMealRemoteDataSource()
    private val analytics = FakeAnalyticsTracker()
    private lateinit var db: TemperoDatabase
    private lateinit var favoriteRepository: FavoriteRepositoryImpl
    private lateinit var mealRepository: MealRepositoryImpl

    @Before
    fun setUp() {
        remote.byId[mealId] = mealDto(
            id = mealId.toString(),
            name = "Beef and Mustard Pie",
            category = "Beef",
            instructions = "Preheat the oven. Mix well.",
            ingredients = listOf("Beef", "Mustard"),
            measures = listOf("1kg", "2 tbs")
        )
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TemperoDatabase::class.java).build()
        favoriteRepository = FavoriteRepositoryImpl(
            MealLocalDataSourceImpl(db.favoriteMealDao()), Dispatchers.IO, Dispatchers.Default
        )
        mealRepository = MealRepositoryImpl(remote, Dispatchers.IO, Dispatchers.Default)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun detailViewModel() = DetailViewModel(
        getMealDetail = GetMealDetailUseCase(favoriteRepository, mealRepository),
        toggleFavorite = ToggleFavoriteUseCase(favoriteRepository, mealRepository),
        favoriteRepository = favoriteRepository,
        analytics = analytics,
        savedStateHandle = SavedStateHandle(mapOf("mealId" to mealId))
    )

    private fun favoritesViewModel() =
        FavoritesViewModel(favoriteRepository, analytics, FakeConnectivityObserver())

    @Test
    fun `favoritar no detalhe aparece em Favoritos e desfavoritar esvazia a lista`() = runBlocking<Unit> {
        val favorites = favoritesViewModel()
        favorites.uiState.awaitValue { it is FavoritesUiState.Empty }

        val detail = detailViewModel()
        val loaded = detail.uiState.awaitValue { it is DetailUiState.Success } as DetailUiState.Success
        assertEquals(false, loaded.isFavorite)
        assertEquals("Beef and Mustard Pie", loaded.meal.name)

        // Favoritar: o detalhe passa a mostrar o coração cheio e Favoritos lista a receita.
        detail.onHandleIntent(DetailIntent.ToggleFavorite)
        detail.uiState.awaitValue { it is DetailUiState.Success && it.isFavorite }
        val listed = favorites.uiState.awaitValue { it is FavoritesUiState.Success } as FavoritesUiState.Success
        assertEquals(listOf("Beef and Mustard Pie"), listed.meals.map { it.name })

        // Desfavoritar pelo detalhe: Favoritos volta a ficar vazio.
        detail.onHandleIntent(DetailIntent.ToggleFavorite)
        detail.uiState.awaitValue { it is DetailUiState.Success && !it.isFavorite }
        favorites.uiState.awaitValue { it is FavoritesUiState.Empty }

        assertEquals(1, analytics.eventsOf<AnalyticsEvent.FavoriteAdded>().size)
        assertEquals(1, analytics.eventsOf<AnalyticsEvent.FavoriteRemoved>().size)
    }

    @Test
    fun `o que vai para o Room e a receita completa e o detalhe passa a funcionar sem API`() = runBlocking<Unit> {
        val detail = detailViewModel()
        detail.uiState.awaitValue { it is DetailUiState.Success }
        detail.onHandleIntent(DetailIntent.ToggleFavorite)
        detail.uiState.awaitValue { it is DetailUiState.Success && it.isFavorite }

        val saved = favoriteRepository.getFavorite(mealId)!!
        assertEquals(listOf("Beef", "Mustard"), saved.ingredients.map { it.ingredient })
        assertTrue(saved.steps.isNotEmpty())

        // Um novo detalhe lê do Room: a API não é consultada de novo.
        val callsBefore = remote.idCalls.size
        remote.error = java.io.IOException("offline")
        val offline = detailViewModel()
        val state = offline.uiState.awaitValue { it is DetailUiState.Success } as DetailUiState.Success

        assertEquals(true, state.isFavorite)
        assertEquals(callsBefore, remote.idCalls.size)
    }

    @Test
    fun `remover em Favoritos atualiza o detalhe e Desfazer devolve a receita`() = runBlocking<Unit> {
        val detail = detailViewModel()
        detail.uiState.awaitValue { it is DetailUiState.Success }
        detail.onHandleIntent(DetailIntent.ToggleFavorite)

        val favorites = favoritesViewModel()
        val listed = favorites.uiState.awaitValue { it is FavoritesUiState.Success } as FavoritesUiState.Success

        favorites.onHandleIntent(FavoritesIntent.Remove(listed.meals.single()))
        favorites.uiState.awaitValue { it is FavoritesUiState.Empty && it.recentlyRemoved != null }
        detail.uiState.awaitValue { it is DetailUiState.Success && !it.isFavorite }

        favorites.onHandleIntent(FavoritesIntent.UndoRemove)
        favorites.uiState.awaitValue { it is FavoritesUiState.Success }
        detail.uiState.awaitValue { it is DetailUiState.Success && it.isFavorite }
    }
}
