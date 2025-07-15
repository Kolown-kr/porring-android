package com.kolown.porring.feature.main.navigation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.main.R

internal enum class MainMenu(
    @DrawableRes
    val iconResId: Int,
    val contentDescription: String,
    val route: MainMenuRoute,
) {
    HOME(
        iconResId = R.drawable.ic_home,
        contentDescription = "Home",
        route = MainMenuRoute.Home,
    ),
    SEARCH(
        iconResId = R.drawable.ic_search,
        contentDescription = "Search",
        route = MainMenuRoute.Search,
    ),
    CAMERA(
        iconResId = R.drawable.ic_add_circle_48dp,
        contentDescription = "Camera",
        route = MainMenuRoute.Camera,
    ),
    FOLLOWER(
        iconResId = R.drawable.ic_follow,
        contentDescription = "Follow",
        route = MainMenuRoute.Follower,
    ),
    MY(
        iconResId = R.drawable.ic_my,
        contentDescription = "My",
        route = MainMenuRoute.My,
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (MainMenuRoute) -> Boolean): MainMenu? {
            return entries.find { predicate(it.route) }
        }

        fun findByRouteName(routeName: String) =
            entries.firstOrNull { it.route::class.simpleName == routeName }


        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries
                .mapNotNull {
                    if (it.route == MainMenuRoute.Camera) null else it.route
                }
                .any { predicate(it) }
        }
    }
}
