package com.kolown.porring.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.search.SearchRoute

fun NavController.navigateSearch(navOptions: NavOptions) {
    navigate(MainMenuRoute.Search, navOptions)
}

fun NavController.navigateSearchDetail(navOptions: NavOptions) {
    navigate(Route.DetailSearch, navOptions = navOptions)
}


fun NavGraphBuilder.searchNavGraph(
    navigateToSearchDetail: (String, String) -> Unit = { _, _ -> }
) {
    composable<MainMenuRoute.Search> {
        SearchRoute(
            navigateToDetail = navigateToSearchDetail
        )
    }
}
