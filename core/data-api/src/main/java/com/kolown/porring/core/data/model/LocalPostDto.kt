package com.kolown.porring.core.data.model

import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.toReactions

data class LocalPostDto(
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

fun LocalPostDto.toPostContentModel() = PostContentModel(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollower = this.isFollower,
    reactions = this.reactions.mapNotNull { it.toReactions() },
    myReaction = this.myReaction?.toReactions()
)