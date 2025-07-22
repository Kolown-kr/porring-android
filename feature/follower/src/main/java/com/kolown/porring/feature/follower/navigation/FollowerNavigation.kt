package com.kolown.porring.feature.follower.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kolown.porring.core.navigation.FollowRoute
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.follower.FollowerRoute
import com.kolown.porring.feature.their.TheirRoute
import com.kolown.porring.feature.follower.FollowerRoute

fun NavController.navigateFollower(navOptions: NavOptions) {
    navigate(MainMenuRoute.Follower, navOptions)
}

fun NavController.navigateFollowerGallery(authorId: String, navOptions: NavOptions? = null) {
    navigate(FollowRoute.FollowGallery(authorId), navOptions)
}

fun NavGraphBuilder.followerNavGraph(
    navigateToLogin: () -> Unit,
    navigateToTheir: (String) -> Unit,
    navigateToDetail: (String, String) -> Unit,
    popBackStack: () -> Unit
) {
    navigation<MainMenuRoute.Follower>(FollowRoute.Follow) {
        composable<FollowRoute.Follow> {
            FollowerRoute(
                navigateToLogin = navigateToLogin,
                navigateToTheir = navigateToTheir
            )
        }

        composable<FollowRoute.FollowGallery> {
            TheirRoute(
                navigateToDetail = navigateToDetail,
                popBackStack = popBackStack
            )
        }
    }
}
