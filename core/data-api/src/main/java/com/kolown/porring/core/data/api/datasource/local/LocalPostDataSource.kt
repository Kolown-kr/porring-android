package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import kotlinx.coroutines.flow.Flow

interface LocalPostDataSource {
    suspend fun insertItems(items: List<OtherPostData>, itemType: PostsUsageType)
    fun getItems(): Flow<List<OtherPostData>>
    fun getPagingItems(): PagingSource<Int, OtherPostData>
    suspend fun getItemById(postId: String): OtherPostData?
    suspend fun clearHomeItems()
    suspend fun clearPagingItems()
    suspend fun getFirstPageItem(): OtherPostData
    suspend fun getLastPageItem(): OtherPostData

    suspend fun insertMyPost(posts: List<MyPostData>)
    fun getMyPosts(): PagingSource<Int, MyPostData>
    suspend fun deleteMyPost(postId: String)
    suspend fun clearMyPost()
}