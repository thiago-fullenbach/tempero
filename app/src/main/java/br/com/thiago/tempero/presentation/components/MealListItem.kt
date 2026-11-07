package br.com.thiago.tempero.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.R
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.presentation.constants.TemperoTheme
import br.com.thiago.tempero.presentation.preview.PreviewData
import coil.compose.AsyncImage

/**
 * Item de lista de receita: miniatura, nome e "categoria · país". O `trailingContent`
 * muda conforme a tela (chevron na busca, coração nos favoritos).
 */
@Composable
fun MealListItem(
    meal: Meal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = {
            AsyncImage(
                model = "${meal.imageUrl}/small",
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        headlineContent = {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "${meal.category.category} · ${meal.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = trailingContent
    )
}

@Preview(showBackground = true)
@Composable
private fun MealListItemPreview() {
    TemperoTheme {
        MealListItem(
            meal = PreviewData.searchResults.first(),
            onClick = {},
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.search_open_recipe),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}
