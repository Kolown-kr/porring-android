package com.kolown.porring.feature.detail_my.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.detail_my.DetailMyRoute

fun NavController.navigateToDetailMy(
    pageIndex: Int,
    navOptions: NavOptions,
) {
    navigate(Route.DetailMy(pageIndex), navOptions = navOptions)
}

fun NavGraphBuilder.detailMyNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.DetailMy> {
        DetailMyRoute(
            padding = padding,
            popBackStack = popBackStack
        )
    }
}
