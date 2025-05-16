package com.kolown.porring.core.network.model

import com.kolown.porring.core.model.Follower


data class FollowerDto(
    val followId: String = "",
    val followerId: String = "",
    val followerName: String = "",
    val userId: String = ""
)

fun FollowerDto.toFollowerModel(): Follower {
    return Follower(
        followerId = followerId,
        followerName = followerName
    )
}
