package com.kolown.porring.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.navigation.SearchRoute
import com.kolown.porring.feature.search.DetailSearchRoute
import com.kolown.porring.feature.search.SearchRoute

fun NavController.navigateSearch(navOptions: NavOptions) {
    navigate(MainMenuRoute.Search, navOptions)
}

fun NavController.navigateSearchDetail(navOptions: NavOptions) {
    navigate(SearchRoute.DetailSearch, navOptions = navOptions)
}


fun NavGraphBuilder.searchNavGraph(
    padding: PaddingValues,
    navigateToTheir: (String) -> Unit,
    isLoggedIn: Boolean,
    
    navigateToSearchDetail: () -> Unit,
    popBackStack: () -> Unit,
    getBackStackEntry: () -> NavBackStackEntry
) {
    navigation<MainMenuRoute.Search>(
        startDestination = SearchRoute.Search,
    ) {
        composable<SearchRoute.Search> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                getBackStackEntry()
            }
            SearchRoute(
                padding = padding,
                imageViewModel = hiltViewModel(parentEntry),
                navigateToDetail = navigateToSearchDetail
            )
        }

        composable<SearchRoute.DetailSearch> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                getBackStackEntry()
            }
            DetailSearchRoute(
                padding = padding,
                imageViewModel = hiltViewModel(parentEntry),
                navigateToTheir = navigateToTheir,
                popBackStack = popBackStack,
                isLoggedIn = isLoggedIn,
            )
        }
    }
}
