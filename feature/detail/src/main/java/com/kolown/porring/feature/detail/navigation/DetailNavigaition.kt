package com.kolown.porring.feature.detail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.detail.DetailRoute

fun NavController.navigateToDetail(
    type: MainMenuRoute.Detail.Type,
    order: Int,
    postId: String? = null,
    navOptions: NavOptions
) {
    navigate(MainMenuRoute.Detail(type, order, postId), navOptions = navOptions)
}

fun NavGraphBuilder.detailNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    navigateToTheir: (String) -> Unit,
) {
    composable<MainMenuRoute.Detail> {
        DetailRoute(
            padding = padding,
            type = it.toRoute<MainMenuRoute.Detail>().type,
            order = it.toRoute<MainMenuRoute.Detail>().order,
            navigateToTheir = navigateToTheir,
            popBackStack = popBackStack,
        )
    }
}
