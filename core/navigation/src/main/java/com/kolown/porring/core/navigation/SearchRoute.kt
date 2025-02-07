package com.kolown.porring.core.navigation

import kotlinx.serialization.Serializable

sealed interface SearchRoute {

    @Serializable
    data object Search : MainMenuRoute

    @Serializable
    data object DetailSearch : SearchRoute
}