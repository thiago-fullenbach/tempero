package br.com.thiago.tempero.presentation.screen.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Ponto de entrada da tela Busca. Por enquanto só guarda a query localmente e o estado
 * fica sempre [SearchUiState.Idle]; o ViewModel e a busca na API virão depois.
 */
@Composable
fun SearchRoute(
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }

    SearchScreen(
        query = query,
        uiState = SearchUiState.Idle,
        onQueryChange = { query = it },
        onBackClick = { onEvent(SearchEvent.Dismissed) },
        onClearClick = { query = "" },
        onMealClick = { onEvent(SearchEvent.MealSelected(it)) },
        onExploreCategoriesClick = { onEvent(SearchEvent.ExploreCategoriesRequested) },
        onRetry = {},
        onSuggestionClick = { query = it },
        onSuggestionFillClick = { query = it },
        modifier = modifier
    )
}
