package com.kolown.porring.core.model

data class FollowerThumbnail(
    val id : String,
    val followerName : String,
    val posts : List<String>
) {
    companion object {
        val dummy = FollowerThumbnail(
            id = "1",
            followerName = "name",
            posts = listOf()
        )
    }
}