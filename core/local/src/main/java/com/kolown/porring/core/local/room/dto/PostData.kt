package com.kolown.porring.core.local.room.dto

data class PostData(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val isFollower: Boolean,
    val reactions: List<Int>,
    val myReaction: Int? = null,
)

