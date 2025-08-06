package com.kolown.porring.feature.detail_their.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.detail_their.DetailTheirRoute

fun NavController.navigateToDetailTheir(
    order: Int,
    navOptions: NavOptions,
    authorId: String = "",
    postId: String? = null
) {
    navigate(Route.DetailTheir(order, authorId, postId), navOptions = navOptions)
}

fun NavGraphBuilder.detailTheirNavGraph(
    popBackStack: () -> Unit,
) {
    composable<Route.DetailTheir> {
        DetailTheirRoute(
            popBackStack = popBackStack,
        )
    }
}
