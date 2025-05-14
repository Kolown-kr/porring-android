package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.local.dto.PostData
import com.kolown.porring.core.local.entity.DefaultPostInfoEntity
import com.kolown.porring.core.local.entity.OtherUserPostInfoEntity
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.toReactions

internal fun PostContentModel.toPostData() = PostData(
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

internal fun PostData.toOtherUserPostInfo() = OtherUserPostInfoEntity(
    postId = postId,
    authorId = authorId,
    isFollower = isFollower,
    myReaction = myReaction,
)

internal fun PostData.toPostDefaultInfo() = DefaultPostInfoEntity(
    postId = postId,
    imageUrl = imageUrl,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions,
)

fun PostData.toPostContentModel() = PostContentModel(
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