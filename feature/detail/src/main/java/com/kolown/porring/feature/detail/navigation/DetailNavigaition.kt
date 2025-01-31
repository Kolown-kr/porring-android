package com.kolown.porring.feature.detail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.detail.DetailRoute
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute

fun NavController.navigateToDetail(navOptions: NavOptions) {
    navigate(MainMenuRoute.Detail, navOptions = navOptions)
}

//@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailNavGraph(
    isLoggedIn: Boolean,

    detailFirstItem: PostContentModel,
    updateMainPostReaction: (PostContentModel, Reactions) -> Unit,
    popBackStack: () -> Unit,
    padding: PaddingValues,
    navigateToTheir : (String) -> Unit,
    updateFollow: (String) -> Unit
) {
    composable<MainMenuRoute.Detail> {
        DetailRoute(
            isLoggedIn = isLoggedIn,
            
            detailFirstItem = detailFirstItem,
            updateMainPostReaction = updateMainPostReaction,
            popBackStack = popBackStack,
            padding = padding,
            navigateToTheir = navigateToTheir,
            updateFollow = updateFollow
        )
    }
}
