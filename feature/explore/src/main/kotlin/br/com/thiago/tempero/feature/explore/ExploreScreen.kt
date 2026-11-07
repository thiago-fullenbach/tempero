package br.com.thiago.tempero.feature.explore

import br.com.thiago.tempero.core.ui.R as UiR
import br.com.thiago.tempero.feature.explore.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.ui.components.StateMessage
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme
import br.com.thiago.tempero.core.ui.preview.PreviewData
import br.com.thiago.tempero.feature.explore.components.CategoryChips
import br.com.thiago.tempero.feature.explore.components.ExploreHeader
import br.com.thiago.tempero.feature.explore.components.ExploreSearchButton
import br.com.thiago.tempero.feature.explore.components.MealCard
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** Quantos itens precisam passar para o botão "voltar ao topo" aparecer. */
private const val ScrollToTopThreshold = 6

private val FullWidth: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

/**
 * Tela Explorar, sem dependência de ViewModel: todo estado entra por parâmetro
 * e todo evento sai por callback.
 */
@Composable
fun ExploreScreen(
    meals: LazyPagingItems<Meal>,
    uiState: ExploreUiState,
    onCategorySelected: (MealCategory?) -> Unit,
    onSearchClick: () -> Unit,
    onMealClick: (Meal) -> Unit,
    onRetry: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        val refreshState = meals.loadState.refresh
        val isRefreshing = refreshState is LoadState.Loading && meals.itemCount == 0
        val hasRefreshError = refreshState is LoadState.Error && meals.itemCount == 0

        val gridState = rememberLazyGridState()
        val isScrolled by remember {
            derivedStateOf { gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0 }
        }

        val scope = rememberCoroutineScope()
        val showScrollToTop by remember { derivedStateOf { gridState.firstVisibleItemIndex > ScrollToTopThreshold } }

        Box {
            Column(modifier = Modifier.statusBarsPadding()) {
                // Título recolhe ao rolar; busca e chips ficam sempre visíveis.
                AnimatedVisibility(visible = !isScrolled) {
                    ExploreHeader(modifier = Modifier.padding(horizontal = Spacing.lg))
                }
                ExploreSearchButton(
                    onClick = onSearchClick,
                    modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.sm)
                )

                when (uiState) {
                    // Categorias ainda carregando: nada além do cabeçalho e da busca.
                    ExploreUiState.Loading -> LoadingIndicator(modifier = Modifier.fillMaxWidth())

                    // Falha ao carregar as categorias: não há filtro nem lista para mostrar.
                    is ExploreUiState.Error -> StateMessage(
                        icon = Icons.Default.Warning,
                        title = stringResource(UiR.string.common_error_title),
                        message = stringResource(UiR.string.common_error_message),
                        iconColor = MaterialTheme.colorScheme.error,
                        iconBackground = MaterialTheme.colorScheme.errorContainer,
                        actionLabel = stringResource(UiR.string.common_retry),
                        onActionClick = onRetry,
                        secondaryActionLabel = stringResource(R.string.explore_see_favorites),
                        onSecondaryActionClick = onFavoritesClick
                    )

                    is ExploreUiState.Success -> {
                        CategoryChips(
                            categories = uiState.categories,
                            selected = uiState.selected,
                            onCategorySelected = onCategorySelected,
                            modifier = Modifier.padding(vertical = Spacing.lg)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            contentPadding = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.xl),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            when {
                                isRefreshing -> item(span = FullWidth) { LoadingIndicator() }
                                hasRefreshError -> item(span = FullWidth) {
                                    StateMessage(
                                        icon = Icons.Default.Warning,
                                        title = stringResource(UiR.string.common_error_title),
                                        message = stringResource(UiR.string.common_error_message),
                                        iconColor = MaterialTheme.colorScheme.error,
                                        iconBackground = MaterialTheme.colorScheme.errorContainer,
                                        actionLabel = stringResource(UiR.string.common_retry),
                                        onActionClick = meals::retry,
                                        secondaryActionLabel = stringResource(R.string.explore_see_favorites),
                                        onSecondaryActionClick = onFavoritesClick
                                    )
                                }
                                refreshState is LoadState.NotLoading && meals.itemCount == 0 -> item(span = FullWidth) {
                                    StateMessage(
                                        icon = Icons.Default.Search,
                                        title = stringResource(R.string.explore_empty_title),
                                        message = stringResource(R.string.explore_empty_message),
                                        iconColor = MaterialTheme.colorScheme.primary,
                                        iconBackground = MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                }
                                else -> {
                                    items(
                                        count = meals.itemCount,
                                        key = meals.itemKey { it.id }
                                    ) { index ->
                                        meals[index]?.let { meal ->
                                            MealCard(meal = meal, onClick = { onMealClick(meal) })
                                        }
                                    }
                                    item(span = FullWidth) { AppendFooter(meals.loadState.append, onRetry = meals::retry) }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(Spacing.lg)
            ) {
                SmallFloatingActionButton(
                    onClick = { scope.launch { gridState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.explore_scroll_to_top)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/** Rodapé da paginação: spinner enquanto carrega a próxima letra, ou botão de nova tentativa. */
@Composable
private fun AppendFooter(
    state: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is LoadState.Loading -> LoadingIndicator(modifier)
        is LoadState.Error -> Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onRetry) { Text(stringResource(UiR.string.common_retry)) }
        }
        is LoadState.NotLoading -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreScreenPreview() {
    TemperoTheme {
        ExploreScreen(
            meals = flowOf(PagingData.from(PreviewData.meals)).collectAsLazyPagingItems(),
            uiState = ExploreUiState.Success(categories = PreviewData.categories),
            onCategorySelected = {},
            onSearchClick = {},
            onMealClick = {},
            onRetry = {},
            onFavoritesClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreScreenLoadingPreview() {
    TemperoTheme {
        ExploreScreen(
            meals = flowOf(PagingData.empty<Meal>()).collectAsLazyPagingItems(),
            uiState = ExploreUiState.Loading,
            onCategorySelected = {},
            onSearchClick = {},
            onMealClick = {},
            onRetry = {},
            onFavoritesClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreScreenErrorPreview() {
    TemperoTheme {
        ExploreScreen(
            meals = flowOf(PagingData.empty<Meal>()).collectAsLazyPagingItems(),
            uiState = ExploreUiState.Error(DataError.Unknown(Exception())),
            onCategorySelected = {},
            onSearchClick = {},
            onMealClick = {},
            onRetry = {},
            onFavoritesClick = {}
        )
    }
}
