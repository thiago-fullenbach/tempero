package br.com.thiago.tempero.presentation.navigation

import br.com.thiago.tempero.R
import br.com.thiago.tempero.core.designsystem.R as DesignsystemR
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

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
        icon = DesignsystemR.drawable.ic_favorite,
        selectedIcon = DesignsystemR.drawable.ic_favorite_filled
    )
}
