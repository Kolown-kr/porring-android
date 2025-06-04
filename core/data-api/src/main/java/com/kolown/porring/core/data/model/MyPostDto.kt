package com.kolown.porring.core.data.model

import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.toReactions

data class MyPostDto(
    val postId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val reactions: List<Int>,
)

fun MyPostDto.toModel() = MyPost(
    postId = this.postId,
    imageUrl = this.imageUrl,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions.mapNotNull { it.toReactions() },
)

fun PostModel.toMyPost() = MyPost(
    postId = postId,
    imageUrl = imageUrl,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions.mapNotNull { it.toReactions() },
)