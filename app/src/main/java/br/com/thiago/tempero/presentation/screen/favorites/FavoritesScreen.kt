package br.com.thiago.tempero.presentation.screen.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.R
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.presentation.components.FavoriteButton
import br.com.thiago.tempero.presentation.components.MealListItem
import br.com.thiago.tempero.presentation.components.OfflineBanner
import br.com.thiago.tempero.presentation.components.StateMessage
import br.com.thiago.tempero.presentation.constants.Spacing
import br.com.thiago.tempero.presentation.constants.TemperoTheme
import br.com.thiago.tempero.presentation.preview.PreviewData
import br.com.thiago.tempero.presentation.screen.favorites.components.FavoritesTopBar

/**
 * Tela Favoritos, sem dependência de ViewModel: todo estado entra por parâmetro
 * e todo evento sai por callback.
 */
@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    isOffline: Boolean,
    onMealClick: (Meal) -> Unit,
    onRemoveClick: (Meal) -> Unit,
    onUndoClick: () -> Unit,
    onRemovalMessageShown: () -> Unit,
    onExploreClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val removedMessage = stringResource(R.string.favorites_removed)
    val undoLabel = stringResource(R.string.favorites_undo)
    val currentOnUndoClick = rememberUpdatedState(onUndoClick)
    val currentOnMessageShown = rememberUpdatedState(onRemovalMessageShown)

    // Uma nova remoção cancela o snackbar anterior (o showSnackbar lança CancellationException).
    LaunchedEffect(uiState.recentlyRemoved) {
        if (uiState.recentlyRemoved != null) {
            val result = snackbarHostState.showSnackbar(
                message = removedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) currentOnUndoClick.value()
            currentOnMessageShown.value()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).statusBarsPadding()) {
            FavoritesTopBar(count = (uiState as? FavoritesUiState.Success)?.meals?.size)

            if (isOffline && (uiState is FavoritesUiState.Success || uiState is FavoritesUiState.Empty)) {
                OfflineBanner(
                    message = stringResource(R.string.favorites_offline_banner),
                    modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.sm)
                )
            }

            when (uiState) {
                FavoritesUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is FavoritesUiState.Error -> StateMessage(
                    icon = Icons.Default.Warning,
                    title = stringResource(R.string.explore_error_title),
                    message = stringResource(R.string.explore_error_message),
                    iconColor = MaterialTheme.colorScheme.error,
                    iconBackground = MaterialTheme.colorScheme.errorContainer,
                    actionLabel = stringResource(R.string.explore_retry),
                    onActionClick = onRetry
                )

                is FavoritesUiState.Empty -> StateMessage(
                    icon = ImageVector.vectorResource(R.drawable.ic_favorite_filled),
                    title = stringResource(R.string.favorites_empty_title),
                    message = stringResource(R.string.favorites_empty_message),
                    iconColor = MaterialTheme.colorScheme.primary,
                    iconBackground = MaterialTheme.colorScheme.surfaceContainerHigh,
                    actionLabel = stringResource(R.string.favorites_explore_recipes),
                    onActionClick = onExploreClick
                )

                is FavoritesUiState.Success -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = uiState.meals, key = { it.id }) { meal ->
                        MealListItem(
                            meal = meal,
                            onClick = { onMealClick(meal) },
                            modifier = Modifier.animateItem(),
                            trailingContent = {
                                FavoriteButton(isFavorite = true, onClick = { onRemoveClick(meal) })
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    TemperoTheme {
        FavoritesScreen(
            uiState = FavoritesUiState.Success(PreviewData.favorites),
            isOffline = false,
            onMealClick = {},
            onRemoveClick = {},
            onUndoClick = {},
            onRemovalMessageShown = {},
            onExploreClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenOfflinePreview() {
    TemperoTheme {
        FavoritesScreen(
            uiState = FavoritesUiState.Success(PreviewData.favorites),
            isOffline = true,
            onMealClick = {},
            onRemoveClick = {},
            onUndoClick = {},
            onRemovalMessageShown = {},
            onExploreClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenEmptyPreview() {
    TemperoTheme {
        FavoritesScreen(
            uiState = FavoritesUiState.Empty(),
            isOffline = false,
            onMealClick = {},
            onRemoveClick = {},
            onUndoClick = {},
            onRemovalMessageShown = {},
            onExploreClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenLoadingPreview() {
    TemperoTheme {
        FavoritesScreen(
            uiState = FavoritesUiState.Loading,
            isOffline = false,
            onMealClick = {},
            onRemoveClick = {},
            onUndoClick = {},
            onRemovalMessageShown = {},
            onExploreClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenErrorPreview() {
    TemperoTheme {
        FavoritesScreen(
            uiState = FavoritesUiState.Error(DataError.Storage(Exception())),
            isOffline = false,
            onMealClick = {},
            onRemoveClick = {},
            onUndoClick = {},
            onRemovalMessageShown = {},
            onExploreClick = {},
            onRetry = {}
        )
    }
}
