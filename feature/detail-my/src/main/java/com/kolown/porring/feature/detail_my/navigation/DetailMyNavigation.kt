package com.kolown.porring.feature.detail_my.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.detail_my.DetailMyRoute

fun NavController.navigateToDetailMy(
    postId: String,
    navOptions: NavOptions,
) {
    navigate(Route.DetailMy(postId), navOptions = navOptions)
}

fun NavGraphBuilder.detailMyNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.DetailMy> {
        DetailMyRoute(
            padding = padding,
            postId = it.toRoute<Route.DetailMy>().postId,
            popBackStack = popBackStack
        )
    }
}
