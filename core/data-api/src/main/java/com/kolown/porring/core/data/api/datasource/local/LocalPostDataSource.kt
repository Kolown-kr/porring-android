package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.MyPostDto
import com.kolown.porring.core.data.model.OtherPostDto
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PostContentModel
import kotlinx.coroutines.flow.Flow

interface LocalPostDataSource {
    suspend fun insertItems(items: List<PostContentModel>, itemType: PostsUsageType)
    fun getItems(): Flow<List<PostContentModel>>
    fun getPagingItems(): PagingSource<Int, OtherPostDto>
    suspend fun getItemById(postId: String): PostContentModel?
    suspend fun clearHomeItems()
    suspend fun clearPagingItems()
    suspend fun getFirstPageItem(): OtherPostDto
    suspend fun getLastPageItem(): OtherPostDto

    suspend fun deleteMyPost(postId: String)

    suspend fun getMyReaction(postId: String): Int?
    suspend fun setPostReaction(postId: String, reaction: Int)
    suspend fun deletePostReaction(postId: String)
    suspend fun insertMyPost(posts: List<MyPost>)
    fun getMyPosts(): PagingSource<Int, MyPostDto>
}