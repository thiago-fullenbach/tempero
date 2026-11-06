package br.com.thiago.tempero.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thiago.tempero.domain.connectivity.ConnectivityObserver
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.FavoriteRepository
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesIntent
import br.com.thiago.tempero.presentation.screen.favorites.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Favoritos vindos do Room. A lista é observada (qualquer mudança no banco atualiza a tela);
 * a última receita removida fica guardada até o snackbar "Desfazer" ser resolvido.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteRepository,
    connectivity: ConnectivityObserver
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val isOffline: StateFlow<Boolean> = connectivity.isOnline
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), false)

    private val recentlyRemoved = MutableStateFlow<Meal?>(null)
    private var observeJob: Job? = null

    init {
        observeFavorites()
    }

    fun onHandleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.Remove -> remove(intent.meal)
            FavoritesIntent.UndoRemove -> undoRemove()
            FavoritesIntent.RemovalMessageShown -> recentlyRemoved.value = null
            FavoritesIntent.Retry -> observeFavorites()
        }
    }

    private fun observeFavorites() {
        observeJob?.cancel()
        _uiState.value = FavoritesUiState.Loading
        observeJob = viewModelScope.launch {
            combine(repository.observeFavorites(), recentlyRemoved) { meals, removed ->
                if (meals.isEmpty()) FavoritesUiState.Empty(removed)
                else FavoritesUiState.Success(meals, removed)
            }
                .catch { _uiState.value = FavoritesUiState.Error(it.toDataError()) }
                .collect { _uiState.value = it }
        }
    }

    private fun remove(meal: Meal) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(meal.id)
                recentlyRemoved.value = meal
            } catch (e: DataError) {
                _uiState.value = FavoritesUiState.Error(e)
            }
        }
    }

    private fun undoRemove() {
        val meal = recentlyRemoved.value ?: return
        recentlyRemoved.value = null
        viewModelScope.launch {
            try {
                repository.addFavorite(meal)
            } catch (e: DataError) {
                _uiState.value = FavoritesUiState.Error(e)
            }
        }
    }

    private fun Throwable.toDataError(): DataError = this as? DataError ?: DataError.Unknown(this)

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
