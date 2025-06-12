package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultLocalPostDataSource @Inject constructor(
    private val postDao: PostDao,
) : LocalPostDataSource {
    override suspend fun insertItems(
        items: List<OtherPostData>,
        itemType: PostsUsageType
    ) {
        postDao.insertItems(items, itemType)
    }

    override suspend fun getFirstPageItem() = postDao.getFirstPageItem()

    override suspend fun getLastPageItem() = postDao.getLastPageItem()

    override fun getItems(): Flow<List<OtherPostData>> {
        return postDao.getHomePosts()
    }

    override fun getPagingItems(): PagingSource<Int, OtherPostData> =
        postDao.getPagingPosts()

    override suspend fun getItemById(postId: String): OtherPostData? {
        return postDao.getItemById(postId)
    }

    override suspend fun clearHomeItems() {
        postDao.clearHomeItems()
    }

    override suspend fun clearPagingItems() {
        postDao.clearPagingItems()
    }


    override fun getMyPosts(): PagingSource<Int, MyPostData> {
        return postDao.getMyPosts()
    }

    override suspend fun insertMyPost(posts: List<MyPostData>) {
        postDao.insertMyPost(posts.map { it.toEntity() })
    }

    override suspend fun deleteMyPost(postId: String) {
        postDao.deleteMyPost(postId)
    }

    override suspend fun clearMyPost() {
        postDao.clearMyPost()
    }
}