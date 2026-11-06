package br.com.thiago.tempero.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.R
import br.com.thiago.tempero.presentation.constants.TemperoTheme

/**
 * Coração de favorito: preenchido em `primary` quando favorito, contorno em
 * `onSurfaceVariant` quando não. Área de toque de 48dp (padrão do [IconButton]).
 */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(
                if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
            ),
            contentDescription = stringResource(
                if (isFavorite) R.string.favorites_remove else R.string.favorites_add
            ),
            tint = if (isFavorite) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteButtonPreview() {
    TemperoTheme {
        FavoriteButton(isFavorite = true, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteButtonOutlinePreview() {
    TemperoTheme {
        FavoriteButton(isFavorite = false, onClick = {})
    }
}
