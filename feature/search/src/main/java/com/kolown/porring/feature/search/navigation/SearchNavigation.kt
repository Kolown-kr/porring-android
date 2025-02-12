package com.kolown.porring.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.search.DetailSearchRoute
import com.kolown.porring.feature.search.SearchRoute

fun NavController.navigateSearch(navOptions: NavOptions) {
    navigate(MainMenuRoute.Search, navOptions)
}

fun NavController.navigateSearchDetail(navOptions: NavOptions) {
    navigate(Route.DetailSearch, navOptions = navOptions)
}


fun NavGraphBuilder.searchNavGraph(
    padding: PaddingValues,
    navigateToSearchDetail: () -> Unit,
) {
    composable<MainMenuRoute.Search> {
        SearchRoute(
            padding = padding,
            navigateToDetail = navigateToSearchDetail
        )
    }
}
