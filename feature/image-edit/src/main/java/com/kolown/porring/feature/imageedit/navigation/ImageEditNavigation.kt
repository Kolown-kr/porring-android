package com.kolown.porring.feature.imageedit.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.imageedit.ImageEditRoute

fun NavController.navigateImageEdit(imgUri: String, navOptions: NavOptions? = null) {
    navigate(Route.ImageEdit(imgUri), navOptions)
}

fun NavGraphBuilder.imageEditNavGraph(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
    navigateToUpload: (String, Float) -> Unit
) {
    composable<Route.ImageEdit> { navBackStackEntry ->
        val imgUri = navBackStackEntry.toRoute<Route.ImageEdit>().imgUri

        ImageEditRoute(
            imgUri = imgUri,
            padding = padding,
            navigateToHome = navigateToHome,
            navigateToUpload = navigateToUpload
        )
    }
}