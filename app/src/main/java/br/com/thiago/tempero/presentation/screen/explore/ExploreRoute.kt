package br.com.thiago.tempero.presentation.screen.explore

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.thiago.tempero.presentation.viewmodel.ExploreViewModel

/**
 * Ponto de entrada da tela Explorar: obtém o ViewModel e guarda o estado da UI,
 * deixando [ExploreScreen] sem estado.
 */
@Composable
fun ExploreRoute(
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val meals = viewModel.meals.collectAsLazyPagingItems()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ExploreScreen(
        meals = meals,
        uiState = uiState.value,
        onCategorySelected = { viewModel.onHandleIntent(ExploreIntent.SelectCategory(it)) },
        onSearchClick = {}, // TODO
        onMealClick = {}, // TODO
        onRetry = { viewModel.onHandleIntent(ExploreIntent.Retry) },
        modifier = modifier
    )
}
