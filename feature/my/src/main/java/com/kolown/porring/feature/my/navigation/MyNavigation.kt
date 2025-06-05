package com.kolown.porring.feature.my.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.my.MyRoute

fun NavController.navigateMy(navOptions: NavOptions) {
    navigate(MainMenuRoute.My, navOptions)
}

fun NavGraphBuilder.myNavGraph(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToDetail: (Int) -> Unit,
) {
    composable<MainMenuRoute.My> {
        MyRoute(
            navigateToLogin = navigateToLogin,
            navigateToSetting = navigateToSetting,
            navigateToDetail = navigateToDetail,
            padding = padding,
            viewModel = hiltViewModel()
        )
    }
}
