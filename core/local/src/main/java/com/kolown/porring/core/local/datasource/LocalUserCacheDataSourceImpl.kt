package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalUserCacheDataSource
import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.data.model.ReactedPostData
import com.kolown.porring.core.local.dao.FollowDao
import com.kolown.porring.core.local.dao.ReactedPostDao
import com.kolown.porring.core.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalUserCacheDataSourceImpl @Inject constructor(
    private val followDao: FollowDao,
    private val reactedPostDao: ReactedPostDao
) : LocalUserCacheDataSource {
    override suspend fun getMyReaction(postId: String): Int? {
        return reactedPostDao.getReactionByPostId(postId)
    }

    override suspend fun insertReactedPosts(reactedPosts: List<ReactedPostData>) {
        reactedPostDao.insertReactedPosts(reactedPosts.map { it.toEntity() })
    }

    override suspend fun deleteReactedPost(postId: String) {
        reactedPostDao.deleteReactedPost(postId)
    }

    override suspend fun clearReactedPost() {
        reactedPostDao.clearReactedPost()
    }

    override suspend fun insertFollows(follows: List<FollowData>) {
        followDao.insertFollows(follows.map { it.toEntity() })
    }

    override fun getFollows(): PagingSource<Int, FollowData> {
        return followDao.getFollows()
    }

    override fun getFollowName(id: String): Flow<String?> {
        return followDao.getFollowName(id)
    }

    override suspend fun updateFollowName(id: String, newName: String) {
        followDao.updateName(id, newName)
    }

    override suspend fun deleteFollow(id: String) {
        followDao.deleteFollow(id)
    }

    override suspend fun clearFollows() {
        followDao.clearFollows()
    }
}