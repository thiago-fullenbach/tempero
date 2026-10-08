package br.com.thiago.tempero.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.thiago.tempero.presentation.constants.Spacing
import br.com.thiago.tempero.presentation.constants.TemperoTheme

/**
 * Layout genérico para estados de erro e vazio: ícone em círculo, título, mensagem
 * e uma ação opcional.
 */
@Composable
fun StateMessage(
    icon: ImageVector,
    title: String,
    message: String,
    iconColor: Color,
    iconBackground: Color,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xxl, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(Modifier.height(Spacing.sm))
            Button(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StateMessageErrorPreview() {
    TemperoTheme {
        StateMessage(
            icon = Icons.Default.Warning,
            title = "No connection",
            message = "We couldn't load the recipes. Check your internet connection and try again.",
            iconColor = MaterialTheme.colorScheme.error,
            iconBackground = MaterialTheme.colorScheme.errorContainer,
            actionLabel = "Try again",
            onActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StateMessageEmptyPreview() {
    TemperoTheme {
        StateMessage(
            icon = Icons.Default.Search,
            title = "Nothing found",
            message = "No recipes available right now.",
            iconColor = MaterialTheme.colorScheme.primary,
            iconBackground = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}
