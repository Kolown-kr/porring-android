package com.kolown.porring.core.navigation

import kotlinx.serialization.Serializable

sealed interface OnBoardRoute: Route {
    
    @Serializable
    data object Login : OnBoardRoute

    @Serializable
    data object Join : OnBoardRoute
}