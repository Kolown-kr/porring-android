package com.kolown.porring.feature.follower.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.follower.FollowerRoute
import com.kolown.porring.core.navigation.MainMenuRoute

fun NavController.navigateFollower(navOptions: NavOptions) {
    navigate(MainMenuRoute.Follower, navOptions)
}

fun NavGraphBuilder.followerNavGraph(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToTheir: (String) -> Unit,
) {
    composable<MainMenuRoute.Follower> {
        FollowerRoute(
            padding = padding,
            navigateToLogin = navigateToLogin,
            navigateToTheir = navigateToTheir
        )
    }
}
