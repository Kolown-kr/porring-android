package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.model.Follower
import kotlinx.coroutines.flow.Flow

interface LocalFollowDataSource {
    suspend fun insertFollowers(followers: List<Follower>)
    fun getFollowerName(followerId: String): Flow<String?>
    fun getFollowers(): PagingSource<Int, Follower>
    suspend fun clearFollowers()
    suspend fun deleteFollower(followerId: String)
}