package com.kolown.porring.core.local

import com.kolown.porring.core.local.room.dto.PostData
import com.kolown.porring.core.local.room.entity.OtherUserPostInfo
import com.kolown.porring.core.local.room.entity.PostDefaultInfo
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

internal fun PostData.toOtherUserPostInfo() = OtherUserPostInfo(
    postId = postId,
    authorId = authorId,
    isFollower = isFollower,
    myReaction = myReaction,
)

internal fun PostData.toPostDefaultInfo() = PostDefaultInfo(
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