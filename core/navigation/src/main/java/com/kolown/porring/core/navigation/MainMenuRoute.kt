package com.kolown.porring.core.navigation

import kotlinx.serialization.Serializable

sealed interface MainMenuRoute : Route {

    @Serializable
    data object Home : MainMenuRoute

    @Serializable
    data object Search : MainMenuRoute

    @Serializable
    data object Follower : MainMenuRoute

    @Serializable
    data object My : MainMenuRoute

}

sealed interface HomeRoute : MainMenuRoute {
    @Serializable
    data object Home : HomeRoute

    @Serializable
    data class HomeGallery(val authorId: String) : HomeRoute
}

sealed interface FollowRoute : MainMenuRoute {
    @Serializable
    data object Follow : FollowRoute

    @Serializable
    data class FollowGallery(val authorId: String) : FollowRoute
}