package com.kolown.porring.feature.main.navigation

import androidx.compose.runtime.Composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route

internal enum class MainMenu(
    val contentDescription: String,
    val route: MainMenuRoute,
) {
    HOME(
        contentDescription = "Home",
        route = MainMenuRoute.Home,
    ),
    SEARCH(
        contentDescription = "Search",
        route = MainMenuRoute.Search,
    ),
    FOLLOWER(
        contentDescription = "Follow",
        route = MainMenuRoute.Follower,
    ),
    MY(
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
                .any { predicate(it.route) }
        }
    }
}
