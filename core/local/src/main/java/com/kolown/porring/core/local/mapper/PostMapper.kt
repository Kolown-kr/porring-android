package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.ReactedPostData
import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.local.entity.ReactedPostEntity

internal fun OtherPostData.toEntity(): OtherPostEntity = OtherPostEntity(
    postId = postId,
    imageUrl = imageUrl,
    imageRatio = imageRatio,
    registerAt = registerAt,
    description = description,
    tags = tags,
    reactions = reactions,
    authorId = authorId,
    myReaction = myReaction,
)

internal fun MyPostData.toEntity() = MyPostEntity(
    postId = this.postId,
    imageUrl = this.imageUrl,
    imageRatio = this.imageRatio,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions,
)

internal fun ReactedPostData.toEntity() = ReactedPostEntity(
    postId = this.postId,
    reaction = this.reaction,
    registerAt = this.registerAt
)