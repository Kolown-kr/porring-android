package com.kolown.porring.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.home.HomeRoute
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(MainMenuRoute.Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    navigateToTheir: (String) -> Unit,
    navigateToDetail: () -> Unit,
) {
    composable<MainMenuRoute.Home> {
        HomeRoute(
            padding = padding,
            navigateToTheir = navigateToTheir,
            navigateToDetail = navigateToDetail,
        )
    }
}
