package br.com.thiago.tempero.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Ponto de entrada da tela Busca: obtém o [SearchViewModel], coleta query e estado e
 * traduz os callbacks de [SearchScreen] em [SearchIntent] ou [SearchEvent].
 */
@Composable
fun SearchEntry(
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query = viewModel.query.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        query = query.value,
        uiState = uiState.value,
        onQueryChange = { viewModel.onHandleIntent(SearchIntent.QueryChanged(it)) },
        onBackClick = { onEvent(SearchEvent.Dismissed) },
        onClearClick = { viewModel.onHandleIntent(SearchIntent.Clear) },
        onMealClick = { onEvent(SearchEvent.MealSelected(it)) },
        onExploreCategoriesClick = { onEvent(SearchEvent.ExploreCategoriesRequested) },
        onRetry = { viewModel.onHandleIntent(SearchIntent.Retry) },
        onSuggestionClick = { viewModel.onHandleIntent(SearchIntent.SuggestionSelected(it)) },
        onSuggestionFillClick = { viewModel.onHandleIntent(SearchIntent.QueryChanged(it)) },
        modifier = modifier
    )
}
