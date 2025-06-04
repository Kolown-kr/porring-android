package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel

internal fun PostContentModel.toModel() = PostModel(
    postId = postId,
    authorId = authorId,
    imageUrl = imageUrl,
    registerAt = registerAt,
    description = description,
    tags = tags,
    isFollower = isFollower,
    reactions = reactions.map { it.value },
    myReaction = myReaction?.value,
)

internal fun PostModel.toEntity() = OtherPostEntity(
    postId = postId,
    imageUrl = imageUrl,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions,
    authorId = authorId,
    myReaction = myReaction,
)

internal fun MyPost.toEntity() = MyPostEntity(
    postId = this.postId,
    imageUrl = this.imageUrl,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions.map { it.value },
)