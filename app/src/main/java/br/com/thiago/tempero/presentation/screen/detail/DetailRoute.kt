package br.com.thiago.tempero.presentation.screen.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.thiago.tempero.presentation.viewmodel.DetailViewModel

/**
 * Ponto de entrada da tela Detalhe: obtém o [DetailViewModel], coleta o estado e
 * traduz os callbacks de [DetailScreen] em [DetailIntent] ou [DetailEvent].
 */
@Composable
fun DetailRoute(
    onEvent: (DetailEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    DetailScreen(
        uiState = uiState.value,
        onBackClick = { onEvent(DetailEvent.BackRequested) },
        onFavoriteClick = { viewModel.onHandleIntent(DetailIntent.ToggleFavorite) },
        onRetry = { viewModel.onHandleIntent(DetailIntent.Retry) },
        modifier = modifier
    )
}
