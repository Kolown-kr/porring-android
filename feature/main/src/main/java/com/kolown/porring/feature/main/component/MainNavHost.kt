package com.kolown.porring.feature.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.camera.navigation.cameraNavGraph
import com.kolown.porring.feature.detail.navigation.detailNavGraph
import com.kolown.porring.feature.detail_my.navigation.detailMyNavGraph
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
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(PorringTheme.colors.background),
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        homeNavGraph(
            navigateToTheir = navigator::navigateToTheir,
            navigateToDetail = {
                navigator.navigateToDetail(
                    MainMenuRoute.Detail.Type.DEFAULT,
                    0
                )
            },
        )

        searchNavGraph(
            navigateToSearchDetail = navigator::navigateToDetailSearch,
        )

        cameraNavGraph(
            navigateToImageEdit = { imgUri ->
                navigator.navigateToImageEdit(imgUri)
            },
            popBackStack = navigator::popBackStack
        )

        followerNavGraph(
            navigateToLogin = navigator::navigateToLogin,
            navigateToTheir = navigator::navigateToTheir
        )

        myNavGraph(
            navigateToLogin = navigator::navigateToLogin,
            navigateToSetting = navigator::navigateToSetting,
            navigateToDetail = navigator::navigateToDetailMy,
        )

        detailNavGraph(
            navigateToTheir = navigator::navigateToTheir,
            popBackStack = navigator::popBackStack
        )

        detailMyNavGraph(
            popBackStack = navigator::popBackStack
        )

        uploadNavGraph(
            navigateToHome = { navigator.navigate(MainMenu.HOME) }
        )

        loginNavGraph(
            popBackStack = navigator::popBackStack,
            navigateToJoin = navigator::navigateToJoin,
        )

        settingNavGraph(
            popBackStack = navigator::popBackStack,
            navigateToUserInfo = navigator::navigateToUserInfo,
            navigateToHome = { navigator.popBackStack(MainMenuRoute.My) },
            navigateToPrivacy = navigator::navigateToPrivacy,
            navigateToDeletedAccount = navigator::navigateToDeletedAccount,
        )

        theirNavGraph(
            popBackStack = navigator::popBackStack,
            navigateToDetail = { authorId, postId ->
                navigator.navigateToDetail(
                    type = MainMenuRoute.Detail.Type.FOLLOW,
                    order = 0,
                    authorId = authorId,
                    postId = postId
                )
            },
        )

        joinNavGraph(
            popBackStack = navigator::popBackStack,
        )

        imageEditNavGraph(
            navigateToHome = { navigator.navigate(MainMenu.HOME) },
            navigateToUpload = { imgUri, imageRatio ->
                navigator.navigateToUpload(
                    imgUri,
                    imageRatio,
                    UploadModel("", 0f, "", emptyList())
                )
            }
        )
    }
}
