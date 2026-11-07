package br.com.thiago.tempero.presentation.screen.explore.components

import br.com.thiago.tempero.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme
import br.com.thiago.tempero.core.ui.preview.PreviewData

/**
 * Chips de filtro por categoria. [selected] nulo representa a opção "Todas".
 */
@Composable
fun CategoryChips(
    categories: List<MealCategory>,
    selected: MealCategory?,
    onCategorySelected: (MealCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        item {
            CategoryChip(
                label = stringResource(R.string.explore_category_all),
                isSelected = selected == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(categories, key = { it.category }) { category ->
            CategoryChip(
                label = category.category,
                isSelected = category == selected,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipsAllSelectedPreview() {
    TemperoTheme {
        CategoryChips(categories = PreviewData.categories, selected = null, onCategorySelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipsSelectedPreview() {
    TemperoTheme {
        CategoryChips(
            categories = PreviewData.categories,
            selected = PreviewData.categories[1],
            onCategorySelected = {}
        )
    }
}
