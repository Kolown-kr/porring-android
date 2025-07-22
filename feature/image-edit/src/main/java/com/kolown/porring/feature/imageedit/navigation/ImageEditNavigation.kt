package com.kolown.porring.feature.imageedit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.CameraRoute
import com.kolown.porring.feature.imageedit.ImageEditRoute

fun NavController.navigateImageEdit(imgUri: String, navOptions: NavOptions? = null) {
    navigate(CameraRoute.ImageEdit(imgUri), navOptions)
}

fun NavGraphBuilder.imageEditNavGraph(
    navigateToHome: () -> Unit,
    navigateToUpload: (String, Float) -> Unit
) {
    composable<CameraRoute.ImageEdit> { navBackStackEntry ->
        val imgUri = navBackStackEntry.toRoute<CameraRoute.ImageEdit>().imgUri

        ImageEditRoute(
            imgUri = imgUri,
            navigateToHome = navigateToHome,
            navigateToUpload = navigateToUpload
        )
    }
}