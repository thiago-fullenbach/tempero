package br.com.thiago.tempero.presentation.screen.search.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.R
import br.com.thiago.tempero.presentation.constants.Spacing
import br.com.thiago.tempero.presentation.constants.TemperoTheme

@Composable
fun SearchResultsCount(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = pluralStringResource(R.plurals.search_results_count, count, count),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.md, bottom = Spacing.xs)
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchResultsCountPreview() {
    TemperoTheme { SearchResultsCount(count = 4) }
}
