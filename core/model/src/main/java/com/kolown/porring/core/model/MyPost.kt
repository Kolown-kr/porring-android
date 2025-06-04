package com.kolown.porring.core.model

data class MyPost(
    val postId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val reactions: List<Reactions>,
)
