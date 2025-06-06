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
    navOptions: NavOptions,
    authorId: String = "",
    postId: String? = null
) {
    navigate(MainMenuRoute.Detail(type, order, authorId, postId), navOptions = navOptions)
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
            authorId = it.toRoute<MainMenuRoute.Detail>().authorId,
            postId = it.toRoute<MainMenuRoute.Detail>().postId,
            navigateToTheir = navigateToTheir,
            popBackStack = popBackStack,
        )
    }
}
