package com.kolown.porring.feature.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.setting.SettingRoute
import com.kolown.porring.feature.setting.delete_account.DeletedAccountRoute
import com.kolown.porring.feature.setting.privacy.PrivacyRoute
import com.kolown.porring.feature.setting.user_info.UserInfoRoute

fun NavController.navigateSetting(navOptions: NavOptions) {
    navigate(Route.Setting, navOptions = navOptions)
}

fun NavController.navigateUserInfo(navOptions: NavOptions) {
    navigate(Route.UserInfo, navOptions = navOptions)
}

fun NavController.navigateDeletedAccount(navOptions: NavOptions) {
    navigate(Route.DeletedAccount, navOptions = navOptions)
}

fun NavController.navigatePrivacy(navOptions: NavOptions) {
    navigate(Route.Privacy, navOptions = navOptions)
}

fun NavGraphBuilder.settingNavGraph(
    popBackStack: () -> Unit,
    updateLoginState: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    navigateToPrivacy: () -> Unit = {},
    navigateToDeletedAccount: () -> Unit = {},
    navigateToUserInfo: () -> Unit = {},
) {
    composable<Route.Setting> {
        SettingRoute(
            popBackStack = popBackStack,
            updateLoginState = updateLoginState,
            navigateToUserInfo = navigateToUserInfo,
        )
    }

    composable<Route.UserInfo> {
        UserInfoRoute(
            popBackStack = popBackStack,
            navigateToHome = navigateToHome,
        )
    }

    composable<Route.DeletedAccount> {
        DeletedAccountRoute(
            navigateToHome = popBackStack,
            popBackStack = popBackStack,
        )
    }

    composable<Route.Privacy> {
        PrivacyRoute(
            popBackStack = popBackStack,
        )
    }
}