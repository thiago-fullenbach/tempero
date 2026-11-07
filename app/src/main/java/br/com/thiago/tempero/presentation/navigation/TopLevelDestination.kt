package br.com.thiago.tempero.presentation.navigation

import br.com.thiago.tempero.R
import br.com.thiago.tempero.core.designsystem.R as DesignsystemR
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.thiago.tempero.feature.explore.navigation.ExploreRoute
import br.com.thiago.tempero.feature.favorites.navigation.FavoritesRoute

/** Destinos da barra de navegação inferior. */
enum class TopLevelDestination(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    Explore(
        route = ExploreRoute,
        label = R.string.nav_explore,
        icon = R.drawable.ic_explore,
        selectedIcon = R.drawable.ic_explore_filled
    ),
    Favorites(
        route = FavoritesRoute,
        label = R.string.nav_favorites,
        icon = DesignsystemR.drawable.ic_favorite,
        selectedIcon = DesignsystemR.drawable.ic_favorite_filled
    )
}
