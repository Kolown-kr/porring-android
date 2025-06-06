package com.kolown.porring.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.camera.navigation.navigateCamera
import com.kolown.porring.feature.detail.navigation.navigateToDetail
import com.kolown.porring.feature.follower.navigation.navigateFollower
import com.kolown.porring.feature.home.navigation.navigateHome
import com.kolown.porring.feature.imageedit.navigation.navigateImageEdit
import com.kolown.porring.feature.join.navigation.navigateToJoin
import com.kolown.porring.feature.login.navigation.navigateLogin
import com.kolown.porring.feature.my.navigation.navigateMy
import com.kolown.porring.feature.search.navigation.navigateSearch
import com.kolown.porring.feature.setting.navigation.navigateSetting
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
            currentDestination?.hasRoute(m::class) == true
        }
    private val singleTopOptions = navOptions {
        launchSingleTop = true
        restoreState = true
    }

    fun navigate(menu: MainMenu) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            launchSingleTop = true
        }

        when (menu) {
            MainMenu.HOME -> navController.navigateHome(navOptions)
            MainMenu.SEARCH -> navController.navigateSearch(navOptions)
            MainMenu.CAMERA -> navController.navigateCamera(navOptions)
            MainMenu.FOLLOWER -> navController.navigateFollower(navOptions)
            MainMenu.MY -> navController.navigateMy(navOptions)
        }
    }

    fun navigateToTheir(authorId: String) =
        navController.navigateTheir(authorId = authorId, navOptions = singleTopOptions)

    fun navigateToImageEdit(imgUri: String) =
        navController.navigateImageEdit(imgUri = imgUri, navOptions = singleTopOptions)

    fun navigateToUpload(imgUri: String, uploadModel: UploadModel) =
        navController.navigateUpload(imgUri, uploadModel)

    fun navigateToDetail(
        type: MainMenuRoute.Detail.Type,
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

    fun navigateToLogin() = navController.navigateLogin(navOptions = singleTopOptions)

    fun navigateToSetting() = navController.navigateSetting(navOptions = singleTopOptions)

    fun navigateToJoin() = navController.navigateToJoin(navOptions = singleTopOptions)

    fun navigateToDetailSearch(tagId: String, postId: String) = navController.navigateToDetail(
        MainMenuRoute.Detail.Type.SEARCH,
        0,
        postId = postId,
        authorId = tagId,
        navOptions = singleTopOptions
    )

    fun popBackStack() {
        navController.previousBackStackEntry?.let {
            navController.popBackStack()
        }
    }

    fun popBackStack(destination: Route) {
        navController.popBackStack(destination, false)
    }

    @Composable
    fun isShowBottomBar() = MainMenu.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
