package com.kolown.porring.core.model

data class FollowWithThumbnail(
    val id: String,
    val followerName: String,
    val thumbnails: List<String>
) {
    companion object {
        val dummy = FollowWithThumbnail(
            id = "1",
            followerName = "name",
            thumbnails = listOf()
        )
    }
}