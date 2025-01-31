package com.kolown.porring.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.feature.home.HomeRoute
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute

fun NavController.navigateHome(navOptions: NavOptions) {
    navigate(MainMenuRoute.Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    isLoggedIn: Boolean,

    mainItems: List<PostContentModel>,
    onSelectReaction: (PostContentModel, Reactions) -> Unit,
    fetchDetailFirst: (PostContentModel) -> Unit,
    updateFollow: (String) -> Unit,
    padding: PaddingValues,
    navigateToTheir: (String) -> Unit,
    navigateToDetail: () -> Unit,
    updateMainItems: () -> Unit
) {
    composable<MainMenuRoute.Home> {
        HomeRoute(
            isLoggedIn = isLoggedIn,

            mainItems = mainItems,
            onSelectReaction = onSelectReaction,
            fetchDetailFirst = fetchDetailFirst,
            updateFollow = updateFollow,
            padding = padding,
            navigateToTheir = navigateToTheir,
            navigateToDetail = navigateToDetail,
            updateMainItems = updateMainItems
        )
    }
}
