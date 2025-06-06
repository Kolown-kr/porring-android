package com.kolown.porring.core.data.mapper

import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.toReactions

internal fun OtherPostData.toPostContentModel() = PostContentModel(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollower = this.isFollowing,
    reactions = this.reactions.mapNotNull { it.toReactions() },
    myReaction = this.myReaction?.toReactions()
)

fun MyPostData.toModel() = MyPost(
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