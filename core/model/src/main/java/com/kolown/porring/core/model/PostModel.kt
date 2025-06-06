package com.kolown.porring.core.model

data class PostModel(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val reactions: List<Int>,
    val myReaction: Int? = null,
    val isFollowing: Boolean = false,
    val random: Long = 0L
)