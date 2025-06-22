package com.kolown.porring.feature.join.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.join.JoinRoute
import com.kolown.porring.core.navigation.Route

fun NavController.navigateToJoin(navOptions: NavOptions) {
    navigate(Route.Join, navOptions = navOptions)
}

fun NavGraphBuilder.joinNavGraph(
    popBackStack: (Route) -> Unit,
) {
    composable<Route.Join> {
        JoinRoute(
            popBackStack = popBackStack,
        )
    }
}
