package com.kolown.porring.feature.login.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.OnBoardRoute
import com.kolown.porring.feature.login.LoginRoute

fun NavController.navigateLogin(navOptions: NavOptions) {
    navigate(OnBoardRoute.Login, navOptions = navOptions)
}

fun NavGraphBuilder.loginNavGraph(
    popBackStack: () -> Unit,
    navigateToJoin: () -> Unit,
) {
    composable<OnBoardRoute.Login> {
        LoginRoute(
            popBackStack = popBackStack,
            navigateToJoin = navigateToJoin,
        )
    }
}
