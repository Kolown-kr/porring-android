package com.kolown.porring.core.data.model

data class OtherPostData(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val isFollowing: Boolean,
    val reactions: List<Int>,
    val myReaction: Int? = null,
)