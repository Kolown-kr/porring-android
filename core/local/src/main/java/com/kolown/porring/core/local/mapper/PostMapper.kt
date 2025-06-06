package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity

internal fun OtherPostData.toEntity(): OtherPostEntity = OtherPostEntity(
    postId = postId,
    imageUrl = imageUrl,
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
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions,
)