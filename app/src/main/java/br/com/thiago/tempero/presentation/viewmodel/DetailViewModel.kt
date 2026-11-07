package br.com.thiago.tempero.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import br.com.thiago.tempero.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.domain.analytics.AnalyticsTracker
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import br.com.thiago.tempero.domain.usecase.GetMealDetailUseCase
import br.com.thiago.tempero.domain.usecase.ToggleFavoriteUseCase
import br.com.thiago.tempero.presentation.navigation.TemperoDestination
import br.com.thiago.tempero.presentation.screen.detail.DetailIntent
import br.com.thiago.tempero.presentation.screen.detail.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Detalhe da receita. A receita vem do Room (se favorita) ou da API; o estado do coração
 * é observado no banco, então a tela se atualiza sozinha depois de favoritar ou desfavoritar.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMealDetail: GetMealDetailUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val analytics: AnalyticsTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mealId: Long = savedStateHandle.toRoute<TemperoDestination.Detail>().mealId

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val meal = MutableStateFlow<Meal?>(null)
    private var loadJob: Job? = null

    init {
        load()
    }

    fun onHandleIntent(intent: DetailIntent) {
        Timber.d("Intent: %s (mealId=%d)", intent, mealId)
        when (intent) {
            DetailIntent.ToggleFavorite -> toggle()
            DetailIntent.Retry -> load()
        }
    }

    private fun load() {
        loadJob?.cancel()
        _uiState.value = DetailUiState.Loading
        Timber.d("Detalhe: carregando mealId=%d", mealId)
        loadJob = viewModelScope.launch {
            try {
                val loaded = getMealDetail(mealId)
                if (loaded == null) {
                    Timber.w("Detalhe: receita %d não encontrada", mealId)
                    _uiState.value = DetailUiState.NotFound
                    return@launch
                }
                meal.value = loaded
                analytics.track(AnalyticsEvent.ViewItem(loaded))
            } catch (e: DataError) {
                Timber.w(e, "Detalhe: falha ao carregar mealId=%d, exibindo estado de erro", mealId)
                analytics.track(AnalyticsEvent.LoadFailed(AnalyticsEvent.SCREEN_DETAIL, e))
                _uiState.value = DetailUiState.Error(e)
                return@launch
            }

            combine(meal.filterNotNull(), favoriteRepository.observeIsFavorite(mealId)) { meal, isFavorite ->
                DetailUiState.Success(meal, isFavorite)
            }
                .catch {
                    Timber.w(it, "Detalhe: falha ao observar favorito de mealId=%d", mealId)
                    _uiState.value = DetailUiState.Error(it.toDataError())
                }
                .collect { _uiState.value = it }
        }
    }

    private fun toggle() {
        val current = meal.value ?: return
        viewModelScope.launch {
            try {
                val isFavorite = toggleFavorite(current)
                Timber.d("Detalhe: mealId=%d agora %s", current.id, if (isFavorite) "favorita" else "não favorita")
                analytics.track(
                    if (isFavorite) AnalyticsEvent.FavoriteAdded(current, AnalyticsEvent.ORIGIN_DETAIL)
                    else AnalyticsEvent.FavoriteRemoved(current.id, AnalyticsEvent.ORIGIN_DETAIL)
                )
            } catch (e: DataError) {
                Timber.w(e, "Detalhe: falha ao alternar favorito de mealId=%d", current.id)
                _uiState.value = DetailUiState.Error(e)
            }
        }
    }

    private fun Throwable.toDataError(): DataError = this as? DataError ?: DataError.Unknown(this)
}
