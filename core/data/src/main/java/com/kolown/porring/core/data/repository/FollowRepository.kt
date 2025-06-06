package com.kolown.porring.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.data.mapper.toData
import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.model.Follow
import com.kolown.porring.core.model.FollowWithThumbnail
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.PostDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

interface FollowRepository {
    suspend fun getFollowerName(followerId: String): Flow<String?>
    suspend fun unFollowUser(followerId: String): Flow<Boolean>
    suspend fun fetchFollows()
    suspend fun clearFollowCache()
    fun followUser(followerId: String, followerName: String): Flow<Boolean>
    suspend fun getFollowsWithPaging(): Flow<PagingData<FollowWithThumbnail>>
}

class FollowRepositoryImpl @Inject constructor(
    private val followDataSource: FollowDataSource,
    private val postDataSource: PostDataSource,
    private val localFollowDataSource: LocalFollowDataSource,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
) : FollowRepository {

    override suspend fun getFollowerName(followerId: String): Flow<String?> {
        return localFollowDataSource.getFollowName(followerId)
    }

    override fun followUser(
        id: String,
        name: String,
    ): Flow<Boolean> = flow {
        val currentUserId = googleAuthDataSource.getUserId()

        localFollowDataSource.insertFollows(
            listOf(
                FollowData(
                    id = id,
                    name = name
                )
            )
        )

        followDataSource.uploadFollow(
            userId = currentUserId,
            follow = Follow(
                id = id,
                name = name
            )
        ).collect { success ->
            emit(success)
        }
    }

    override suspend fun unFollowUser(
        id: String,
    ): Flow<Boolean> = flow {
        val currentUserId = googleAuthDataSource.getUserId()

        localFollowDataSource.deleteFollow(id)
        followDataSource.removeFollow(
            userId = currentUserId,
            followerId = id
        ).collect { success ->
            emit(success)
        }
    }

    override suspend fun fetchFollows() {
        val userId = googleAuthDataSource.getUserId()
        val follows = followDataSource.fetchFollows(userId)

        localFollowDataSource.insertFollows(follows.map { it.toData() })
    }

    override suspend fun clearFollowCache() {
        localFollowDataSource.clearFollows()
    }

    override suspend fun getFollowsWithPaging(): Flow<PagingData<FollowWithThumbnail>> =
        withContext(Dispatchers.IO) {
            return@withContext Pager(
                config = PagingConfig(
                    pageSize = FOLLOWER_PER_PAGE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = { localFollowDataSource.getFollows() }
            ).flow.map { pagingData ->
                pagingData.map { follow ->
                    val posts =
                        postDataSource.fetchPostWithAuthorId(follow.id, 4).getOrThrow()

                    FollowWithThumbnail(
                        id = follow.id,
                        followerName = follow.name,
                        thumbnails = posts.map { it.imageUrl }
                    )
                }
            }
        }

    companion object {
        const val FOLLOWER_PER_PAGE = 5
    }
}
