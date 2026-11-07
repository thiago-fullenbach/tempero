package br.com.thiago.tempero.presentation.screen.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.presentation.constants.Spacing
import br.com.thiago.tempero.presentation.constants.TemperoTheme
import br.com.thiago.tempero.presentation.preview.PreviewData

/** Nome da receita e chips de categoria e área (só texto; a área some se vier vazia). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailHeader(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg, end = Spacing.lg, top = 20.dp, bottom = Spacing.sm)
    ) {
        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(top = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            MetadataChip(text = meal.category.category)
            if (meal.area.isNotBlank()) MetadataChip(text = meal.area)
        }
    }
}

@Composable
private fun MetadataChip(text: String) {
    AssistChip(
        onClick = {},
        label = { Text(text = text, style = MaterialTheme.typography.labelLarge) }
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailHeaderPreview() {
    TemperoTheme {
        DetailHeader(meal = PreviewData.detailMeal)
    }
}
