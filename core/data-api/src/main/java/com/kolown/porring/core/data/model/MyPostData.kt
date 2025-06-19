package com.kolown.porring.core.data.model

data class MyPostData(
    val postId: String,
    val imageUrl: String,
    val imageRatio: Float,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val reactions: List<Int>,
)