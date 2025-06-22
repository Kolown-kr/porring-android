package com.kolown.porring.feature.camera.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.camera.screen.CameraRoute

fun NavController.navigateCamera(navOptions: NavOptions) {
    navigate(MainMenuRoute.Camera, navOptions)
}

fun NavGraphBuilder.cameraNavGraph(
    navigateToImageEdit: (String) -> Unit = {},
    popBackStack: () -> Unit = {}
) {
    composable<MainMenuRoute.Camera> {
        CameraRoute(
            navigateToImageEdit = navigateToImageEdit,
            popBackStack = popBackStack
        )
    }
}
