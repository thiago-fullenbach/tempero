package br.com.thiago.tempero.presentation.screen.explore.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.presentation.constants.TemperoTheme
import br.com.thiago.tempero.presentation.preview.PreviewData
import coil.compose.AsyncImage

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        AsyncImage(
            model = "${meal.imageUrl}/medium",
            contentDescription = meal.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        )
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp)) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${meal.category.category} · ${meal.area}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealCardPreview() {
    TemperoTheme {
        MealCard(
            meal = PreviewData.meals.first(),
            onClick = {},
            modifier = Modifier.width(160.dp)
        )
    }
}
