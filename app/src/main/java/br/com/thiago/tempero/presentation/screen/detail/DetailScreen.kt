package br.com.thiago.tempero.presentation.screen.detail

import br.com.thiago.tempero.R
import br.com.thiago.tempero.core.ui.R as UiR
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.core.model.DataError
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.ui.components.StateMessage
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme
import br.com.thiago.tempero.core.ui.preview.PreviewData
import br.com.thiago.tempero.presentation.screen.detail.components.DetailHeader
import br.com.thiago.tempero.presentation.screen.detail.components.DetailHero
import br.com.thiago.tempero.presentation.screen.detail.components.IngredientRow
import br.com.thiago.tempero.presentation.screen.detail.components.StepItem

/**
 * Tela Detalhe, sem dependência de ViewModel: todo estado entra por parâmetro
 * e todo evento sai por callback. Tela cheia, sem barra de navegação.
 */
@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                DetailUiState.Loading -> StateContent(onBackClick = onBackClick) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DetailUiState.Error -> StateContent(onBackClick = onBackClick) {
                    StateMessage(
                        icon = Icons.Default.Warning,
                        title = stringResource(UiR.string.common_error_title),
                        message = stringResource(UiR.string.common_error_message),
                        iconColor = MaterialTheme.colorScheme.error,
                        iconBackground = MaterialTheme.colorScheme.errorContainer,
                        actionLabel = stringResource(UiR.string.common_retry),
                        onActionClick = onRetry
                    )
                }

                DetailUiState.NotFound -> StateContent(onBackClick = onBackClick) {
                    StateMessage(
                        icon = Icons.Default.Search,
                        title = stringResource(R.string.detail_not_found_title),
                        message = stringResource(R.string.detail_not_found_message),
                        iconColor = MaterialTheme.colorScheme.primary,
                        iconBackground = MaterialTheme.colorScheme.surfaceContainerHigh,
                        actionLabel = stringResource(R.string.detail_go_back),
                        onActionClick = onBackClick,
                        tonalAction = true
                    )
                }

                is DetailUiState.Success -> DetailContent(
                    meal = uiState.meal,
                    isFavorite = uiState.isFavorite,
                    onBackClick = onBackClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    meal: Meal,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    // Ingredientes marcados (índices); sobrevive à rotação e à morte do processo.
    val checked = rememberSaveable(
        meal.id,
        saver = listSaver(save = { it.toList() }, restore = { it.toMutableStateList() })
    ) { mutableStateListOf<Int>() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            DetailHero(
                meal = meal,
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )
        }
        item { DetailHeader(meal = meal) }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.detail_ingredients),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(
                        R.string.detail_ingredients_progress,
                        checked.size,
                        meal.ingredients.size
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        itemsIndexed(items = meal.ingredients) { index, ingredient ->
            IngredientRow(
                ingredient = ingredient,
                checked = index in checked,
                onCheckedChange = { isChecked ->
                    if (isChecked) checked.add(index) else checked.remove(index)
                }
            )
        }
        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
            )
        }
        item {
            Text(
                text = stringResource(R.string.detail_instructions),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.md, bottom = Spacing.xs)
            )
        }
        itemsIndexed(items = meal.steps) { index, step ->
            StepItem(number = index + 1, text = step.step)
        }
        item {
            Spacer(Modifier.height(Spacing.xl).navigationBarsPadding())
        }
    }
}

/** Moldura dos estados sem conteúdo: botão de voltar no topo e o estado ocupando o resto. */
@Composable
private fun StateContent(
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        IconButton(onClick = onBackClick, modifier = Modifier.padding(Spacing.sm)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.detail_back)
            )
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    TemperoTheme {
        DetailScreen(
            uiState = DetailUiState.Success(PreviewData.detailMeal, isFavorite = true),
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenNotFavoritePreview() {
    TemperoTheme {
        DetailScreen(
            uiState = DetailUiState.Success(PreviewData.detailMeal, isFavorite = false),
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenLoadingPreview() {
    TemperoTheme {
        DetailScreen(
            uiState = DetailUiState.Loading,
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenErrorPreview() {
    TemperoTheme {
        DetailScreen(
            uiState = DetailUiState.Error(DataError.Network(Exception())),
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenNotFoundPreview() {
    TemperoTheme {
        DetailScreen(
            uiState = DetailUiState.NotFound,
            onBackClick = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}
