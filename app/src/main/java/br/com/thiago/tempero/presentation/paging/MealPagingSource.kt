package br.com.thiago.tempero.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.thiago.tempero.data.MealRepositoryImpl
import br.com.thiago.tempero.domain.model.Meal

class MealPagingSource(
    private val repository: MealRepositoryImpl
): PagingSource<Int, Meal>() {
    private val letters = ('a'..'z').map { it }

    override fun getRefreshKey(state: PagingState<Int, Meal>): Int? =
        state.anchorPosition?.let { state.closestPageToPosition(it) }?.let {
            it.prevKey?.plus(1) ?: it.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Meal> = try {
        val index = params.key ?: 0
        val meals = repository.searchMealsByFirstLetter(letters[index])

        LoadResult.Page(
            data = meals,
            prevKey = if(index == 0) null else index - 1,
            nextKey = if(index == letters.lastIndex) null else index + 1
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}