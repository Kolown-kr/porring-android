package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.local.entity.DefaultPostInfoEntity
import com.kolown.porring.core.local.entity.OtherUserPostInfoEntity
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.toReactions

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

internal fun PostModel.toOtherUserPostInfo() = OtherUserPostInfoEntity(
    postId = postId,
    authorId = authorId,
    isFollower = isFollower,
    myReaction = myReaction,
)

internal fun PostModel.toPostDefaultInfo() = DefaultPostInfoEntity(
    postId = postId,
    imageUrl = imageUrl,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions,
)