package br.com.thiago.tempero.feature.search

import br.com.thiago.tempero.core.ui.R as UiR
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.ui.components.MealListItem
import br.com.thiago.tempero.core.ui.components.StateMessage
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme
import br.com.thiago.tempero.core.ui.preview.PreviewData
import br.com.thiago.tempero.feature.search.components.SearchHeader
import br.com.thiago.tempero.feature.search.components.SearchIdleContent
import br.com.thiago.tempero.feature.search.components.SearchResultsCount

/**
 * Tela Busca (barra expandida), sem dependência de ViewModel: todo estado entra por
 * parâmetro e todo evento sai por callback.
 */
@Composable
fun SearchScreen(
    query: String,
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onMealClick: (Meal) -> Unit,
    onExploreCategoriesClick: () -> Unit,
    onRetry: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSuggestionFillClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            SearchHeader(
                query = query,
                onQueryChange = onQueryChange,
                onBackClick = onBackClick,
                onClearClick = onClearClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            when (uiState) {
                SearchUiState.Idle -> SearchIdleContent(
                    onSuggestionClick = onSuggestionClick,
                    onSuggestionFillClick = onSuggestionFillClick
                )

                SearchUiState.Loading -> LoadingIndicator(modifier = Modifier.fillMaxSize())

                is SearchUiState.Error -> StateMessage(
                    icon = Icons.Default.Warning,
                    title = stringResource(UiR.string.common_error_title),
                    message = stringResource(UiR.string.common_error_message),
                    iconColor = MaterialTheme.colorScheme.error,
                    iconBackground = MaterialTheme.colorScheme.errorContainer,
                    actionLabel = stringResource(UiR.string.common_retry),
                    onActionClick = onRetry
                )

                SearchUiState.Empty -> StateMessage(
                    icon = Icons.Default.Search,
                    title = stringResource(R.string.search_empty_title),
                    message = stringResource(R.string.search_empty_message, query),
                    iconColor = MaterialTheme.colorScheme.primary,
                    iconBackground = MaterialTheme.colorScheme.surfaceContainerHigh,
                    actionLabel = stringResource(R.string.search_explore_categories),
                    onActionClick = onExploreCategoriesClick,
                    tonalAction = true
                )

                is SearchUiState.Success -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { SearchResultsCount(count = uiState.meals.size) }
                    items(items = uiState.meals, key = { it.id }) { meal ->
                        MealListItem(
                            meal = meal,
                            onClick = { onMealClick(meal) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(UiR.string.search_open_recipe),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
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
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenResultsPreview() {
    TemperoTheme {
        SearchScreen(
            query = "chicken",
            uiState = SearchUiState.Success(PreviewData.searchResults),
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onMealClick = {},
            onExploreCategoriesClick = {},
            onRetry = {},
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenEmptyPreview() {
    TemperoTheme {
        SearchScreen(
            query = "feijoada",
            uiState = SearchUiState.Empty,
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onMealClick = {},
            onExploreCategoriesClick = {},
            onRetry = {},
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenLoadingPreview() {
    TemperoTheme {
        SearchScreen(
            query = "chicken",
            uiState = SearchUiState.Loading,
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onMealClick = {},
            onExploreCategoriesClick = {},
            onRetry = {},
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenIdlePreview() {
    TemperoTheme {
        SearchScreen(
            query = "",
            uiState = SearchUiState.Idle,
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onMealClick = {},
            onExploreCategoriesClick = {},
            onRetry = {},
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenErrorPreview() {
    TemperoTheme {
        SearchScreen(
            query = "chicken",
            uiState = SearchUiState.Error(DataError.Unknown(Exception())),
            onQueryChange = {},
            onBackClick = {},
            onClearClick = {},
            onMealClick = {},
            onExploreCategoriesClick = {},
            onRetry = {},
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}
