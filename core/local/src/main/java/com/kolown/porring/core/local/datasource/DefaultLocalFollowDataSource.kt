package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.local.dao.FollowDao
import com.kolown.porring.core.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultLocalFollowDataSource @Inject constructor(
    private val followDao: FollowDao
) : LocalFollowDataSource {
    override suspend fun insertFollows(follows: List<FollowData>) {
        followDao.insertFollows(follows.map { it.toEntity() })
    }

    override fun getFollows(): PagingSource<Int, FollowData> {
        return followDao.getFollows()
    }

    override fun getFollowName(id: String): Flow<String?> {
        return followDao.getFollowName(id)
    }

    override suspend fun clearFollows() {
        followDao.clearFollows()
    }

    override suspend fun deleteFollow(id: String) {
        followDao.deleteFollow(id)
    }
}