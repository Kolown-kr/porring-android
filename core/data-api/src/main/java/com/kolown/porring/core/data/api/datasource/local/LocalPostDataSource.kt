package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import kotlinx.coroutines.flow.Flow

interface LocalPostDataSource {
    suspend fun insertItems(items: List<OtherPostData>, itemType: PostsUsageType)

    fun getHomePosts(): Flow<List<OtherPostData>>
    fun getRandomPosts(): PagingSource<Int, OtherPostData>
    fun getGalleryPosts(): PagingSource<Int, OtherPostData>

    suspend fun getItemById(postId: String): OtherPostData?

    suspend fun clearHomePosts()
    suspend fun clearRandomPosts()
    suspend fun clearGalleryPosts()

    suspend fun getFirstRandomItem(): OtherPostData
    suspend fun getLastRandomItem(): OtherPostData

    suspend fun getLastGalleryItem(): OtherPostData

    suspend fun insertMyPost(posts: List<MyPostData>)
    fun getMyPosts(): PagingSource<Int, MyPostData>
    suspend fun deleteMyPost(postId: String)
    suspend fun clearMyPost()
}