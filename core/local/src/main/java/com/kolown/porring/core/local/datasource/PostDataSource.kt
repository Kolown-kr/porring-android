package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.model.LocalItemType
import com.kolown.porring.core.data.model.LocalPostDto
import com.kolown.porring.core.data.model.toPostContentModel
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.mapper.toModel
import com.kolown.porring.core.model.PostContentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomePostDataSourceImpl @Inject constructor(
    private val postDao: PostDao,
) : LocalPostDataSource {
    override suspend fun insertItems(
        items: List<PostContentModel>,
        itemType: LocalItemType
    ) {
        postDao.insertItems(items.map { it.toModel() }, itemType)
    }

    override suspend fun getFirstPageItem() = postDao.getFirstPageItem()

    override suspend fun getLastPageItem() = postDao.getLastPageItem()

    override fun getItems(): Flow<List<PostContentModel>> {
        return postDao.getItems().map { items -> items.map { it.toPostContentModel() } }
    }

    override fun getPagingItems(useRegisterAt: Boolean): PagingSource<Int, LocalPostDto> =
        postDao.getPagingItems(useRegisterAt)

    override suspend fun getItemById(postId: String): PostContentModel? {
        return postDao.getItemById(postId)?.toPostContentModel()
    }

    override suspend fun clearHomeItems() {
        postDao.clearHomeItems()
    }

    override suspend fun clearPagingItems() {
        postDao.clearPagingItems()
    }

    override suspend fun deleteMyPost(postId: String) {
        postDao.deleteMyPost(postId)
    }

    override suspend fun getMyReaction(postId: String): Int? {
        val post = postDao.getItemById(postId) ?: return null

        return post.myReaction
    }

    override suspend fun setPostReaction(postId: String, reaction: Int) {
        val post = postDao.getItemById(postId) ?: return
        val myReaction = post.myReaction
        val reactions = post.reactions.toMutableList()

        myReaction?.let { reactions.remove(it) }
        reactions.add(0, reaction)

        postDao.updateReactions(postId, reactions.toList())
        postDao.updateMyReaction(postId, reaction)
    }

    override suspend fun deletePostReaction(postId: String) {
        val post = postDao.getItemById(postId) ?: return
        val reactions = post.reactions.toMutableList()

        reactions.remove(post.myReaction)

        postDao.updateReactions(postId, reactions.toList())
        postDao.updateMyReaction(postId, null)
    }
}