package br.com.thiago.tempero.presentation.screen.favorites

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.thiago.tempero.presentation.viewmodel.FavoritesViewModel

/**
 * Ponto de entrada da tela Favoritos: obtém o [FavoritesViewModel], coleta estado e
 * conectividade e traduz os callbacks de [FavoritesScreen] em [FavoritesIntent] ou [FavoritesEvent].
 */
@Composable
fun FavoritesRoute(
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isOffline = viewModel.isOffline.collectAsStateWithLifecycle()

    FavoritesScreen(
        uiState = uiState.value,
        isOffline = isOffline.value,
        onMealClick = { onEvent(FavoritesEvent.MealSelected(it)) },
        onRemoveClick = { viewModel.onHandleIntent(FavoritesIntent.Remove(it)) },
        onUndoClick = { viewModel.onHandleIntent(FavoritesIntent.UndoRemove) },
        onRemovalMessageShown = { viewModel.onHandleIntent(FavoritesIntent.RemovalMessageShown) },
        onExploreClick = { onEvent(FavoritesEvent.ExploreRequested) },
        onRetry = { viewModel.onHandleIntent(FavoritesIntent.Retry) },
        modifier = modifier
    )
}
