package com.kolown.porring.feature.their.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.their.DetailTheirRoute
import com.kolown.porring.feature.their.TheirRoute

fun NavController.navigateTheir(authorId: String, navOptions: NavOptions? = null) {
    navigate(MainMenuRoute.Their(authorId), navOptions)
}

fun NavController.navigateTheirDetail(navOptions: NavOptions) {
    navigate(Route.DetailTheir, navOptions)
}

fun NavGraphBuilder.theirNavGraph(
    popBackStack: () -> Unit,
    navigateToDetailTheir: () -> Unit,
    padding: PaddingValues,
) {
    composable<MainMenuRoute.Their> { navBackStackEntry ->
        val followerId = navBackStackEntry.toRoute<MainMenuRoute.Their>().authorId

        TheirRoute(
            followerId = followerId,
            padding = padding,
            viewModel = hiltViewModel(),
            navigateToDetailTheir = navigateToDetailTheir,
            popBackStack = popBackStack
        )
    }
}