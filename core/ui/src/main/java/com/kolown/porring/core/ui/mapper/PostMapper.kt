package com.kolown.porring.core.ui.mapper

import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.toReactions
import com.kolown.porring.core.ui.model.PostUiModel

fun PostModel.toUiModel() = PostUiModel(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollowing = this.isFollowing,
    reactions = this.reactions.mapNotNull { it.toReactions() },
    myReaction = this.myReaction?.toReactions(),
)

fun PostUiModel.toModel() = PostModel(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollowing = this.isFollowing,
    reactions = this.reactions.map { it.value },
    myReaction = this.myReaction?.value
)
