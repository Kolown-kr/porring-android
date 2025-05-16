package com.kolown.porring.core.data.api.datasource.local

import com.kolown.porring.core.model.Follower

interface LocalFollowDataSource {
    suspend fun insertFollowers(followers: List<Follower>)

    suspend fun getFollowers(): List<Follower>

    suspend fun clearFollowers()

    suspend fun deleteFollower(followerId: String)
}