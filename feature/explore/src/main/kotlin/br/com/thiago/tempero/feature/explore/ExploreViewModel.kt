package br.com.thiago.tempero.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import br.com.thiago.tempero.core.domain.analytics.AnalyticsEvent
import br.com.thiago.tempero.core.domain.analytics.AnalyticsTracker
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.domain.repository.MealRepository
import br.com.thiago.tempero.feature.explore.paging.MealPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: MealRepository,
    private val analytics: AnalyticsTracker
) : ViewModel() {
    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val allMeals = MutableStateFlow<List<Meal>?>(null)

    private val selected = _uiState
        .map { (it as? ExploreUiState.Success)?.selected }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals: Flow<PagingData<Meal>> =
        combine(allMeals.filterNotNull(), selected) { all, cat -> all to cat }
            .flatMapLatest { (all, cat) ->
                Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
                    MealPagingSource(meals = all, selectedCategory = cat)
                }.flow
            }
            .cachedIn(viewModelScope)

    init {
        loadMeals()
    }

    fun onHandleIntent(intent: ExploreIntent) {
        Timber.d("Intent: %s", intent)
        when(intent) {
            ExploreIntent.Retry -> loadMeals()
            is ExploreIntent.SelectCategory -> onCategorySelected(intent.category)
        }
    }

    private fun loadMeals() = viewModelScope.launch {
        _uiState.value = ExploreUiState.Loading
        try {
            val cats = repository.listCategories()
            allMeals.value = repository.getAllMeals(cats)
            _uiState.value = ExploreUiState.Success(cats)
            Timber.d("Explorar carregado: %d categorias, %d receitas", cats.size, allMeals.value?.size ?: 0)
        } catch (e: DataError) {
            Timber.w(e, "Explorar: falha ao carregar, exibindo estado de erro")
            analytics.track(AnalyticsEvent.LoadFailed(AnalyticsEvent.SCREEN_EXPLORE, e))
            _uiState.value = ExploreUiState.Error(e)
        }
    }

    private fun onCategorySelected(c: MealCategory?) {
        analytics.track(AnalyticsEvent.CategorySelected(c?.category))
        _uiState.update {
            if (it is ExploreUiState.Success) it.copy(selected = c) else it
        }
    }
}
