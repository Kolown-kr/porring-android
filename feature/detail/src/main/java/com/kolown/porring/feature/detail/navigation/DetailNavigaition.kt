package com.kolown.porring.feature.detail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.detail.DetailRoute
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute

fun NavController.navigateToDetail(navOptions: NavOptions) {
    navigate(MainMenuRoute.Detail, navOptions = navOptions)
}

fun NavGraphBuilder.detailNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    navigateToTheir : (String) -> Unit,
) {
    composable<MainMenuRoute.Detail> {
        DetailRoute(
            padding = padding,
            navigateToTheir = navigateToTheir,
            popBackStack = popBackStack,
        )
    }
}
