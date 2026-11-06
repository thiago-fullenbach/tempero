package br.com.thiago.tempero.presentation.screen.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.R
import br.com.thiago.tempero.presentation.constants.Spacing
import br.com.thiago.tempero.presentation.constants.TemperoTheme

/** Conteúdo da busca vazia: sugestões fixas, sem nada a carregar. */
@Composable
fun SearchIdleContent(
    onSuggestionClick: (String) -> Unit,
    onSuggestionFillClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.search_suggestions_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = 20.dp, bottom = 10.dp)
        )
        stringArrayResource(R.array.search_suggestions).forEach { suggestion ->
            SearchSuggestionItem(
                suggestion = suggestion,
                onClick = { onSuggestionClick(suggestion) },
                onFillClick = { onSuggestionFillClick(suggestion) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchIdleContentPreview() {
    TemperoTheme {
        SearchIdleContent(
            onSuggestionClick = {},
            onSuggestionFillClick = {}
        )
    }
}
