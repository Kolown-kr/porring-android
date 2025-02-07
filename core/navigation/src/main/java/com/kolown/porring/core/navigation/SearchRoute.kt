package com.kolown.porring.core.navigation

import kotlinx.serialization.Serializable

sealed interface SearchRoute: Route {

    @Serializable
    data object Search : SearchRoute

    @Serializable
    data object DetailSearch : SearchRoute
}