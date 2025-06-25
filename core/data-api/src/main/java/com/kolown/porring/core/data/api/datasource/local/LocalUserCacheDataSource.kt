package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.data.model.ReactedPostData
import kotlinx.coroutines.flow.Flow

interface LocalUserCacheDataSource {
    suspend fun getMyReaction(postId: String): Int?
    suspend fun insertReactedPosts(reactedPosts: List<ReactedPostData>)
    suspend fun deleteReactedPost(postId: String)
    suspend fun clearReactedPost()

    suspend fun insertFollows(follows: List<FollowData>)
    fun getFollowName(id: String): Flow<String?>
    suspend fun updateFollowName(id: String, newName: String)
    fun getFollows(): PagingSource<Int, FollowData>
    suspend fun deleteFollow(id: String)
    suspend fun clearFollows()
}