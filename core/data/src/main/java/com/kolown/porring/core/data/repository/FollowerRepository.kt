package com.kolown.porring.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.data.datasource.paging.FollowerGalleryThumbnailPagingDataSource
import com.kolown.porring.core.model.Follower
import com.kolown.porring.core.model.FollowerThumbnail
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.PostDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

interface FollowRepository {
    suspend fun getFollowerName(followerId: String): Flow<String>
    suspend fun unFollowUser(followerId: String): Flow<Boolean>
    suspend fun fetchFollows()
    suspend fun clearFollowCache()
    fun followUser(followerId: String, followerName: String): Flow<Boolean>
    fun getFollowerDataSourcePagingFlow(): Flow<PagingData<FollowerThumbnail>>
}

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource,
    private val postDataSource: PostDataSource,
    private val localFollowDataSource: LocalFollowDataSource,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
) : FollowRepository {

    override suspend fun getFollowerName(followerId: String): Flow<String> {
        val currentUserId = googleAuthDataSource.getUserId()

        return followDataSource.getFollowerName(currentUserId, followerId)
    }

    override fun followUser(
        followerId: String,
        followerName: String,
    ): Flow<Boolean> = flow {
        val currentUserId = googleAuthDataSource.getUserId()

        localFollowDataSource.insertFollowers(
            listOf(
                Follower(
                    followerId = followerId,
                    followerName = followerName
                )
            )
        )

        followDataSource.uploadFollow(
            userId = currentUserId,
            follower = Follower(
                followerId = followerId,
                followerName = followerName
            )
        ).collect { success ->
            emit(success)
        }
    }

    override suspend fun unFollowUser(
        followerId: String,
    ): Flow<Boolean> = flow {
        val currentUserId = googleAuthDataSource.getUserId()

        localFollowDataSource.deleteFollower(followerId)
        followDataSource.removeFollow(
            userId = currentUserId,
            followerId = followerId
        ).collect { success ->
            emit(success)
        }
    }

    override suspend fun fetchFollows() {
        val userId = googleAuthDataSource.getUserId()
        val follows = followDataSource.fetchFollows(userId)

        localFollowDataSource.insertFollowers(follows)
    }

    override suspend fun clearFollowCache() {
        localFollowDataSource.clearFollowers()
    }

    override fun getFollowerDataSourcePagingFlow(): Flow<PagingData<FollowerThumbnail>> {
        val currentUserId = googleAuthDataSource.getUserId()

        return Pager(
            config = PagingConfig(
                pageSize = FOLLOWER_PER_PAGE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                FollowerGalleryThumbnailPagingDataSource(
                    followDataSource,
                    postDataSource,
                    currentUserId
                )
            }
        ).flow
    }

    companion object {
        const val FOLLOWER_PER_PAGE = 5
    }

}
