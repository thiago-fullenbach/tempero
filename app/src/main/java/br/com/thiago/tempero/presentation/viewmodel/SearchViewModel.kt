package br.com.thiago.tempero.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.repository.MealRepository
import br.com.thiago.tempero.presentation.screen.search.SearchIntent
import br.com.thiago.tempero.presentation.screen.search.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

/**
 * Busca por nome (`search.php?s=`): pesquisa ao digitar com debounce de [SEARCH_DEBOUNCE_MS]
 * e mínimo de [MIN_QUERY_LENGTH] letras. Abaixo disso o estado é [SearchUiState.Idle], sem API.
 * A query é guardada no [SavedStateHandle] para sobreviver à morte do processo.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MealRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeQuery()
    }

    fun onHandleIntent(intent: SearchIntent) {
        // QueryChanged dispara a cada tecla; o log fica em searchNow, depois do debounce.
        if (intent !is SearchIntent.QueryChanged) Timber.d("Intent: %s", intent)
        when (intent) {
            is SearchIntent.QueryChanged -> savedStateHandle[KEY_QUERY] = intent.query

            SearchIntent.Clear -> {
                savedStateHandle[KEY_QUERY] = ""
                searchJob?.cancel()
                _uiState.value = SearchUiState.Idle
            }

            is SearchIntent.SuggestionSelected -> {
                savedStateHandle[KEY_QUERY] = intent.query
                searchNow(intent.query.trim())
            }

            SearchIntent.Retry -> searchNow(query.value.trim())
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            query
                .map { it.trim() }
                .debounce(SEARCH_DEBOUNCE_MS.milliseconds)
                .distinctUntilChanged()
                .collectLatest { searchNow(it).join() }
        }
    }

    /** Cancela a busca anterior e dispara uma nova, respeitando o tamanho mínimo. */
    private fun searchNow(q: String): Job {
        searchJob?.cancel()
        return viewModelScope.launch {
            if (q.length < MIN_QUERY_LENGTH) {
                _uiState.value = SearchUiState.Idle
                return@launch
            }
            _uiState.value = SearchUiState.Loading
            Timber.d("Buscando '%s'", q)
            _uiState.value = try {
                val meals = repository.searchMeals(q)
                if (meals.isEmpty()) {
                    Timber.d("Busca '%s': sem resultados", q)
                    SearchUiState.Empty
                } else {
                    Timber.d("Busca '%s': %d resultados", q, meals.size)
                    SearchUiState.Success(meals)
                }
            } catch (e: DataError) {
                Timber.w(e, "Busca '%s': falha, exibindo estado de erro", q)
                SearchUiState.Error(e)
            }
        }.also { searchJob = it }
    }

    private companion object {
        const val KEY_QUERY = "query"
        const val SEARCH_DEBOUNCE_MS = 400L
        const val MIN_QUERY_LENGTH = 2
    }
}
