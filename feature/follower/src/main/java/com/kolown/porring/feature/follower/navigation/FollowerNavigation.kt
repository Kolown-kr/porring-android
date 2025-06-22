package com.kolown.porring.feature.follower.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.follower.FollowerRoute

fun NavController.navigateFollower(navOptions: NavOptions) {
    navigate(MainMenuRoute.Follower, navOptions)
}

fun NavGraphBuilder.followerNavGraph(
    navigateToLogin: () -> Unit,
    navigateToTheir: (String) -> Unit,
) {
    composable<MainMenuRoute.Follower> {
        FollowerRoute(
            navigateToLogin = navigateToLogin,
            navigateToTheir = navigateToTheir
        )
    }
}
