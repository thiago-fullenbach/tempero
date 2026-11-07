package br.com.thiago.tempero.feature.explore.components

import br.com.thiago.tempero.feature.explore.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme

@Composable
fun ExploreHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.sm, bottom = Spacing.sm)
    ) {
        Text(
            text = stringResource(R.string.explore_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.explore_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreHeaderPreview() {
    TemperoTheme { ExploreHeader() }
}
