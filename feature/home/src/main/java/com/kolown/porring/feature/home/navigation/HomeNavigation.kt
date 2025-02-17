package com.kolown.porring.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.home.HomeRoute

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
            navigateToDetail = navigateToDetail,
            navigateToTheir = navigateToTheir,
        )
    }
}
