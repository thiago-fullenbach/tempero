package br.com.thiago.tempero.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory

class MealPagingSource(
    private val selectedCategory: MealCategory? = null,
    meals: List<Meal>,
) : PagingSource<Int, Meal>() {

    private val items =
        if (selectedCategory == null) meals else meals.filter { it.category == selectedCategory }

    override fun getRefreshKey(state: PagingState<Int, Meal>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Meal> {
        val start = params.key ?: 0
        val end = minOf(start + params.loadSize, items.size)
        return LoadResult.Page(
            data = if (start < end) items.subList(start, end) else emptyList(),
            prevKey = null,
            nextKey = if (end < items.size) end else null
        )
    }
}