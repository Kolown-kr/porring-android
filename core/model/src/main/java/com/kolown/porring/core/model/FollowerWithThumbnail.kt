package com.kolown.porring.core.model

data class FollowerWithThumbnail(
    val id: String,
    val followerName: String,
    val thumbnails: List<String>
) {
    companion object {
        val dummy = FollowerWithThumbnail(
            id = "1",
            followerName = "name",
            thumbnails = listOf()
        )
    }
}