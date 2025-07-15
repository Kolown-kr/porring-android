package com.kolown.porring.feature.their.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.their.TheirRoute

fun NavController.navigateTheir(authorId: String, navOptions: NavOptions? = null) {
    navigate(MainMenuRoute.Their(authorId), navOptions)
}

fun NavGraphBuilder.theirNavGraph(
    popBackStack: () -> Unit,
    navigateToDetail: (String, String) -> Unit,
) {
    composable<MainMenuRoute.Their> {
        TheirRoute(
            viewModel = hiltViewModel(),
            navigateToDetail = navigateToDetail,
            popBackStack = popBackStack
        )
    }
}