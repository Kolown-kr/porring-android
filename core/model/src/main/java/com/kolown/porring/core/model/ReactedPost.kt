package com.kolown.porring.core.model

data class ReactedPost(
    val postId: String,
    val reaction: Int,
    val registerAt: String,
)