package br.com.thiago.tempero.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.thiago.tempero.presentation.constants.TemperoTheme

/** Barra de navegação inferior (Explorar / Favoritos); o item ativo usa o ícone preenchido. */
@Composable
fun TemperoBottomBar(
    selected: TopLevelDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { destination ->
            val isSelected = destination == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (isSelected) destination.selectedIcon else destination.icon
                        ),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.label)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemperoBottomBarPreview() {
    TemperoTheme {
        TemperoBottomBar(selected = TopLevelDestination.Favorites, onDestinationSelected = {})
    }
}
