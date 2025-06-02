package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.local.dao.FollowerDao
import com.kolown.porring.core.local.mapper.toEntity
import com.kolown.porring.core.model.Follower
import javax.inject.Inject

class DefaultLocalFollowDataSource @Inject constructor(
    private val followerDao: FollowerDao
) : LocalFollowDataSource {
    override suspend fun insertFollowers(followers: List<Follower>) {
        followerDao.insertFollowers(followers.map { it.toEntity() })
    }

    override fun getFollowers(): PagingSource<Int, Follower> {
        return followerDao.getFollowers()
    }

    override suspend fun clearFollowers() {
        followerDao.clearFollowers()
    }

    override suspend fun deleteFollower(followerId: String) {
        followerDao.deleteFollower(followerId)
    }
}