package com.kolown.porring.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kolown.porring.core.navigation.HomeRoute
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.home.HomeRoute
import com.kolown.porring.feature.their.TheirRoute

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(MainMenuRoute.Home, navOptions)
}

fun NavController.navigateHomeGallery(authorId: String, navOptions: NavOptions? = null) {
    navigate(HomeRoute.HomeGallery(authorId), navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    navigateToTheir: (String) -> Unit,
    navigateToDetail: () -> Unit,
    navigateToGalleryDetail: (String, String) -> Unit,
    popBackStack: () -> Unit
) {
    navigation<MainMenuRoute.Home>(HomeRoute.Home) {
        composable<HomeRoute.Home> {
            HomeRoute(
                navigateToDetail = navigateToDetail,
                navigateToTheir = navigateToTheir,
            )
        }

        composable<HomeRoute.HomeGallery> {
            TheirRoute(
                navigateToDetail = navigateToGalleryDetail,
                popBackStack = popBackStack
            )
        }
    }
}
