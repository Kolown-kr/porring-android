package com.kolown.porring.core.network.model

import com.kolown.porring.core.model.Follow


data class FollowerDto(
    val followId: String = "",
    val followerId: String = "",
    val followerName: String = "",
    val userId: String = ""
)

fun FollowerDto.toFollowerModel(): Follow {
    return Follow(
        id = followerId,
        name = followerName
    )
}
