package com.kolown.porring.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.navigation.toRouteClassOrNull
import com.kolown.porring.feature.camera.navigation.navigateCamera
import com.kolown.porring.feature.detail.navigation.navigateToDetail
import com.kolown.porring.feature.detail_my.navigation.navigateToDetailMy
import com.kolown.porring.feature.detail_their.navigation.navigateToDetailTheir
import com.kolown.porring.feature.follower.navigation.navigateFollower
import com.kolown.porring.feature.follower.navigation.navigateFollowerGallery
import com.kolown.porring.feature.home.navigation.navigateHome
import com.kolown.porring.feature.home.navigation.navigateHomeGallery
import com.kolown.porring.feature.imageedit.navigation.navigateImageEdit
import com.kolown.porring.feature.join.navigation.navigateToJoin
import com.kolown.porring.feature.login.navigation.navigateLogin
import com.kolown.porring.feature.my.navigation.navigateMy
import com.kolown.porring.feature.search.navigation.navigateSearch
import com.kolown.porring.feature.setting.navigation.navigateDeletedAccount
import com.kolown.porring.feature.setting.navigation.navigatePrivacy
import com.kolown.porring.feature.setting.navigation.navigateSetting
import com.kolown.porring.feature.setting.navigation.navigateUserInfo
import com.kolown.porring.feature.their.navigation.navigateTheir
import com.kolown.porring.feature.upload.navigation.navigateUpload

internal class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination = MainMenu.HOME.route
    internal val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentMenu: MainMenu?
        @Composable get() = MainMenu.find { m ->
            currentDestination
                ?.hierarchy
                ?.any { navDestination ->
                    navDestination.route?.contains(m::class.qualifiedName.orEmpty()) == true
                } == true
        }
    private val singleTopOptions = navOptions {
        launchSingleTop = true
        restoreState = true
    }

    fun navigateMainMenu(menu: MainMenu) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            launchSingleTop = true
        }

        when (menu) {
            MainMenu.HOME -> navController.navigateHome(navOptions)
            MainMenu.SEARCH -> navController.navigateSearch(navOptions)
            MainMenu.FOLLOWER -> navController.navigateFollower(navOptions)
            MainMenu.MY -> navController.navigateMy(navOptions)
        }
    }

    fun navigateToHomeGallery(authorId: String) =
        navController.navigateHomeGallery(authorId = authorId, navOptions = singleTopOptions)

    fun navigateToFollowerGallery(authorId: String) =
        navController.navigateFollowerGallery(authorId = authorId, navOptions = singleTopOptions)

    fun navigateToCamera() = navController.navigateCamera(navOptions = singleTopOptions)

    fun navigateToTheir(authorId: String) =
        navController.navigateTheir(authorId = authorId, navOptions = singleTopOptions)

    fun navigateToImageEdit(imgUri: String) =
        navController.navigateImageEdit(imgUri = imgUri, navOptions = singleTopOptions)

    fun navigateToUpload(imgUri: String, imageRatio: Float, uploadModel: UploadModel) =
        navController.navigateUpload(imgUri, imageRatio, uploadModel)

    fun navigateToDetail(
        type: Route.Detail.Type,
        order: Int,
        authorId: String = "",
        postId: String? = null
    ) =
        navController.navigateToDetail(
            type = type,
            order = order,
            authorId = authorId,
            postId = postId,
            navOptions = singleTopOptions
        )

    fun navigateToDetailTheir(
        order: Int,
        authorId: String = "",
        postId: String? = null
    ) =
        navController.navigateToDetailTheir(
            order = order,
            authorId = authorId,
            postId = postId,
            navOptions = singleTopOptions
        )


    fun navigateToDetailMy(pageIndex: Int) =
        navController.navigateToDetailMy(pageIndex = pageIndex, navOptions = singleTopOptions)

    fun navigateToLogin() = navController.navigateLogin(navOptions = singleTopOptions)

    fun navigateToSetting() = navController.navigateSetting(navOptions = singleTopOptions)

    fun navigateToJoin() = navController.navigateToJoin(navOptions = singleTopOptions)

    fun navigateToDetailSearch(tagId: String, postId: String) = navController.navigateToDetail(
        Route.Detail.Type.SEARCH,
        0,
        postId = postId,
        authorId = tagId,
        navOptions = singleTopOptions
    )

    fun navigateToUserInfo() = navController.navigateUserInfo(navOptions = singleTopOptions)

    fun navigateToPrivacy() = navController.navigatePrivacy(navOptions = singleTopOptions)

    fun navigateToDeletedAccount() =
        navController.navigateDeletedAccount(navOptions = singleTopOptions)

    fun popBackStack() {
        navController.previousBackStackEntry?.let {
            navController.popBackStack()
        }
    }

    fun popBackStack(destination: Route) = navController.popBackStack(destination, false)

    @Composable
    fun isShowBottomBar() = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.hierarchy
        ?.mapNotNull { it.route?.toRouteClassOrNull() }
        ?.firstOrNull { MainMenuRoute::class.java.isAssignableFrom(it.java) } != null
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
