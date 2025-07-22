package com.kolown.porring.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.detail.DetailRoute

fun NavController.navigateToDetail(
    type: Route.Detail.Type,
    order: Int,
    navOptions: NavOptions,
    authorId: String = "",
    postId: String? = null
) {
    navigate(Route.Detail(type, order, authorId, postId), navOptions = navOptions)
}

fun NavGraphBuilder.detailNavGraph(
    popBackStack: () -> Unit,
    navigateToTheir: (String) -> Unit,
) {
    composable<Route.Detail> {
        DetailRoute(
            type = it.toRoute<Route.Detail>().type,
            order = it.toRoute<Route.Detail>().order,
            authorId = it.toRoute<Route.Detail>().authorId,
            postId = it.toRoute<Route.Detail>().postId,
            navigateToTheir = navigateToTheir,
            popBackStack = popBackStack,
        )
    }
}
