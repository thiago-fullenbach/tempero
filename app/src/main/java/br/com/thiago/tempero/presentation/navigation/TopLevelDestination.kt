package br.com.thiago.tempero.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.thiago.tempero.R

/** Destinos da barra de navegação inferior. */
enum class TopLevelDestination(
    val route: TemperoDestination,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    Explore(
        route = TemperoDestination.Explore,
        label = R.string.nav_explore,
        icon = R.drawable.ic_explore,
        selectedIcon = R.drawable.ic_explore_filled
    ),
    Favorites(
        route = TemperoDestination.Favorites,
        label = R.string.nav_favorites,
        icon = R.drawable.ic_favorite,
        selectedIcon = R.drawable.ic_favorite_filled
    )
}
