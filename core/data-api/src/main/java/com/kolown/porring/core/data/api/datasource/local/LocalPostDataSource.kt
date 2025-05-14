package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.LocalItemType
import com.kolown.porring.core.data.model.LocalPostDto
import com.kolown.porring.core.model.PostContentModel
import kotlinx.coroutines.flow.Flow

interface LocalPostDataSource {
    suspend fun insertItems(items: List<PostContentModel>, itemType: LocalItemType)
    fun getItems(): Flow<List<PostContentModel>>
    fun getPagingItems(useRegisterAt: Boolean = false): PagingSource<Int, LocalPostDto>
    suspend fun getItemById(postId: String): PostContentModel?
    suspend fun clearHomeItems()
    suspend fun updateReaction(postId: String, reaction: Int)
    suspend fun updateFollowState(authorId: String, isFollow: Boolean)
    suspend fun clearPagingItems()
    suspend fun getFirstPageItem(): LocalPostDto
    suspend fun getLastPageItem(): LocalPostDto
}