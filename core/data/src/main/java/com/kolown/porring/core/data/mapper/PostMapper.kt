package com.kolown.porring.core.data.mapper

import com.kolown.porring.core.data.dto.ReactedPostDto
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.ReactedPostData
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.ReactedPost
import com.kolown.porring.core.model.toReactions

internal fun OtherPostData.toModel() = PostModel(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollowing = this.isFollowing,
    reactions = this.reactions,
    myReaction = this.myReaction
)

internal fun PostModel.toOtherData() = OtherPostData(
    postId = this.postId,
    authorId = this.authorId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    isFollowing = this.isFollowing,
    reactions = this.reactions,
    myReaction = this.myReaction
)

internal fun PostModel.toMyData() = MyPostData(
    postId = this.postId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions,
)

internal fun MyPostData.toModel() = MyPost(
    postId = this.postId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions.mapNotNull { it.toReactions() },
)

internal fun ReactedPost.toData() = ReactedPostData(
    postId = this.postId,
    reaction = this.reaction,
    registerAt = this.registerAt
)

internal fun ReactedPostDto.toModel() = ReactedPost(
    postId = this.postId,
    reaction = this.reaction,
    registerAt = this.registerAt
)

fun PostModel.toMyPost() = MyPost(
    postId = postId,
    imageUrl = imageUrl,
    imageRatio = imageRatio,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions.mapNotNull { it.toReactions() },
)