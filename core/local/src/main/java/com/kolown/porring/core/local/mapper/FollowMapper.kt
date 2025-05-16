package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.local.entity.FollowerEntity
import com.kolown.porring.core.model.Follower

internal fun Follower.toEntity(): FollowerEntity {
    return FollowerEntity(
        followerId = followerId,
        name = followerName,
    )
}