package com.kolown.porring.feature.login.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.login.LoginRoute
import com.kolown.porring.core.navigation.Route

fun NavController.navigateLogin(navOptions: NavOptions) {
    navigate(Route.Login, navOptions = navOptions)
}

fun NavGraphBuilder.loginNavGraph(
    popBackStack: () -> Unit,
    navigateToJoin: () -> Unit,
) {
    composable<Route.Login> {
        LoginRoute(
            popBackStack = popBackStack,
            navigateToJoin = navigateToJoin,
        )
    }
}
