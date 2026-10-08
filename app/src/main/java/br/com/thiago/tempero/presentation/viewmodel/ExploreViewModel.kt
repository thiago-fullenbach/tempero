package br.com.thiago.tempero.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.repository.MealRepository
import br.com.thiago.tempero.presentation.paging.MealPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {
    val meals: Flow<PagingData<Meal>> = Pager(PagingConfig(pageSize = 20)) {
        MealPagingSource(repository)
    }.flow
}