package com.kolown.porring.feature.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.feature.camera.navigation.cameraNavGraph
import com.kolown.porring.feature.detail.navigation.detailNavGraph
import com.kolown.porring.feature.follower.navigation.followerNavGraph
import com.kolown.porring.feature.home.navigation.homeNavGraph
import com.kolown.porring.feature.join.navigation.joinNavGraph
import com.kolown.porring.feature.login.navigation.loginNavGraph
import com.kolown.porring.feature.main.navigation.MainMenu
import com.kolown.porring.feature.main.navigation.MainNavigator
import com.kolown.porring.feature.my.navigation.myNavGraph
import com.kolown.porring.feature.search.navigation.searchNavGraph
import com.kolown.porring.feature.setting.navigation.settingNavGraph
import com.kolown.porring.feature.their.navigation.theirNavGraph
import com.kolown.porring.feature.upload.navigation.uploadNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            homeNavGraph(
                padding = padding,
                navigateToTheir = navigator::navigateToTheir,
                navigateToDetail = navigator::navigateToDetail,
            )

            searchNavGraph(
                padding = padding,
                navigateToTheir = { id ->
                    navigator.navigateToTheir(id)
                },
                navigateToSearchDetail = navigator::navigateToDetailSearch,
                popBackStack = navigator::popBackStack,
                getBackStackEntry = { navigator.navController.getBackStackEntry(MainMenu.SEARCH.route) }
            )

            cameraNavGraph(
                navigateToUpload = { imgUri ->
                    navigator.navigateToUpload(
                        imgUri,
                        UploadModel("", "", emptyList())
                    )
                },
                padding = padding,
                popBackStack = navigator::popBackStack
            )

            followerNavGraph(
                padding = padding,
                navigateToLogin = navigator::navigateToLogin,
                navigateToTheir = navigator::navigateToTheir
            )

            myNavGraph(
                padding = padding,
                navigateToLogin = navigator::navigateToLogin,
                navigateToSetting = navigator::navigateToSetting,
                navigateToDetailMy = navigator::navigateToDetailMy,
                popBackStack = navigator::popBackStack,
                getBackStackEntry = { navigator.navController.getBackStackEntry(MainMenu.HOME.route) }
            )

            detailNavGraph(
                padding = padding,
                navigateToTheir = navigator::navigateToTheir,
                popBackStack = navigator::popBackStack
            )

            uploadNavGraph(
                padding = padding,
                navigateToHome = { navigator.navigate(MainMenu.HOME) }
            )

            loginNavGraph(
                popBackStack = navigator::popBackStack,
                navigateToJoin = navigator::navigateToJoin,
                padding = padding
            )

            settingNavGraph(
                popBackStack = navigator::popBackStack,
                padding = padding
            )

            theirNavGraph(
                popBackStack = navigator::popBackStack,
                navigateToDetailTheir = navigator::navigateToDetailTheir,
                getBackStackEntry = { navigator.navController.getBackStackEntry(MainMenu.HOME.route) },
                padding = padding
            )

            joinNavGraph(
                popBackStack = navigator::popBackStack,
                padding = padding
            )
        }
    }
}
