package com.kolown.porring.core.navigation

import com.kolown.porring.core.model.UploadModel
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data class ImageEdit(val imgUri: String) : Route

    @Serializable
    data class Upload(val imgUri: String, val imageRatio: Float, val uploadModel: UploadModel) : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Setting : Route

    @Serializable
    data object Join : Route

    @Serializable
    data object DetailSearch : Route

    @Serializable
    data class DetailMy(val pageIndex: Int) : Route

    @Serializable
    data object DetailTheir : Route

    @Serializable
    data object DeletedAccount : Route
}

sealed interface MainMenuRoute : Route {
    @Serializable
    data object Home : MainMenuRoute

    @Serializable
    data object Search : MainMenuRoute

    @Serializable
    data object Camera : MainMenuRoute

    @Serializable
    data object Follower : MainMenuRoute

    @Serializable
    data object My : MainMenuRoute

    @Serializable
    data class Their(val authorId: String) : MainMenuRoute

    @Serializable
    data class Detail(
        val type: Type,
        val order: Int,
        val authorId: String,
        val postId: String? = null,
    ) :
        MainMenuRoute {
        @Serializable
        enum class Type {
            DEFAULT, MY, FOLLOW, SEARCH
        }
    }

}


