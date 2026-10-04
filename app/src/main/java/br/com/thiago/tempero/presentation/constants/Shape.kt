package br.com.thiago.tempero.presentation.constants

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // snackbar
    small = RoundedCornerShape(8.dp),        // chips, thumbnails
    medium = RoundedCornerShape(12.dp),      // cards
    large = RoundedCornerShape(16.dp),       // FAB
    extraLarge = RoundedCornerShape(28.dp)   // busca, bottom sheet
)

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp   // margem lateral padrão
    val xl = 24.dp
    val xxl = 32.dp
}