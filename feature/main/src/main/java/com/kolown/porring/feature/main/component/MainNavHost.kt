package com.kolown.porring.feature.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.camera.navigation.cameraNavGraph
import com.kolown.porring.feature.detail.navigation.detailNavGraph
import com.kolown.porring.feature.follower.navigation.followerNavGraph
import com.kolown.porring.feature.home.navigation.homeNavGraph
import com.kolown.porring.feature.imageedit.navigation.imageEditNavGraph
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
                navigateToDetail = {
                    navigator.navigateToDetail(
                        MainMenuRoute.Detail.Type.DEFAULT,
                        0
                    )
                },
            )

            searchNavGraph(
                padding = padding,
                navigateToSearchDetail = navigator::navigateToDetailSearch,
            )

            cameraNavGraph(
                navigateToImageEdit = { imgUri ->
                    navigator.navigateToImageEdit(imgUri)
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
                navigateToDetail = { postId ->
                    navigator.navigateToDetail(
                        MainMenuRoute.Detail.Type.MY,
                        0,
                        postId
                    )
                },
            )

            LazyListState
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
                navigateToDetail = { postId ->
                    navigator.navigateToDetail(
                        MainMenuRoute.Detail.Type.FOLLOW,
                        0,
                        postId
                    )
                },
                padding = padding
            )

            joinNavGraph(
                popBackStack = navigator::popBackStack,
                padding = padding
            )

            imageEditNavGraph(
                padding = padding,
                navigateToHome = { navigator.navigate(MainMenu.HOME) },
                navigateToUpload = { imgUri ->
                    navigator.navigateToUpload(
                        imgUri,
                        UploadModel("", "", emptyList())
                    )
                }
            )
        }
    }
}
