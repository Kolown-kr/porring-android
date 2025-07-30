package com.kolown.porring.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Setting : Route

    @Serializable
    data object DetailSearch : Route

    @Serializable
    data object DeletedAccount : Route

    @Serializable
    data object Privacy : Route

    @Serializable
    data object UserInfo : Route

    @Serializable
    data class Their(val authorId: String) : Route

    @Serializable
    data class Detail(
        val type: Type,
        val order: Int,
        val authorId: String,
        val postId: String? = null,
    ) : Route {
        @Serializable
        enum class Type {
            DEFAULT, FOLLOW, SEARCH
        }
    }

    @Serializable
    data class DetailMy(val pageIndex: Int) : Route
}
