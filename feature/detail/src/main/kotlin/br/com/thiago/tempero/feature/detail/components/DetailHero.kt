package br.com.thiago.tempero.feature.detail.components

import br.com.thiago.tempero.feature.detail.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.ui.components.FavoriteButton
import br.com.thiago.tempero.core.designsystem.Spacing
import br.com.thiago.tempero.core.designsystem.TemperoTheme
import br.com.thiago.tempero.core.ui.preview.PreviewData
import coil.compose.AsyncImage

/**
 * Foto da receita (260dp, passa por trás da status bar) com os botões de voltar e de
 * favorito por cima, em `surface` a 85% para ficarem legíveis sobre qualquer foto.
 */
@Composable
fun DetailHero(
    meal: Meal,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        AsyncImage(
            model = "${meal.imageUrl}/large",
            contentDescription = meal.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(Spacing.sm),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onBackClick, colors = buttonColors) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.detail_back)
                )
            }
            Box(modifier = Modifier.weight(1f))
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                colors = buttonColors,
                inactiveTint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailHeroPreview() {
    TemperoTheme {
        DetailHero(
            meal = PreviewData.detailMeal,
            isFavorite = true,
            onBackClick = {},
            onFavoriteClick = {}
        )
    }
}
