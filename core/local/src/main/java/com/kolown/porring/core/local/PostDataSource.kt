package com.kolown.porring.core.local

import com.kolown.porring.core.local.room.dao.PostDao
import com.kolown.porring.core.model.PostContentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LocalPostDataSource {
    suspend fun insertItems(items: List<PostContentModel>)
    fun getItems(): Flow<List<PostContentModel>>
    suspend fun getItemById(postId: String): PostContentModel?
    suspend fun clearHomeItems()
    suspend fun updateReaction(postId: String, reaction: Int)
    suspend fun updateFollowState(authorId: String, isFollow: Boolean)
}

class HomePostDataSourceImpl @Inject constructor(
    private val postDao: PostDao
) : LocalPostDataSource {
    override suspend fun insertItems(items: List<PostContentModel>) {
        postDao.insertItems(items.map { it.toPostDto() })
    }

    override fun getItems(): Flow<List<PostContentModel>> {
        return postDao.getItems().map { items -> items.map { it.toPostContentModel() } }
    }

    override suspend fun updateReaction(postId: String, reaction: Int) {
        val post = postDao.getItemById(postId) ?: return
        val myReaction = if (post.myReaction == reaction) null else reaction
        val reactions = post.reactions.toMutableList()

        reactions.remove(post.myReaction)
        if (myReaction != null) reactions.add(0, reaction)

        postDao.updateReactions(postId, reactions.toList())
        postDao.updateMyReaction(postId, myReaction)
    }

    override suspend fun updateFollowState(authorId: String, isFollow: Boolean) {
        postDao.updateFollowState(authorId, isFollow)
    }

    override suspend fun getItemById(postId: String): PostContentModel? {
        return postDao.getItemById(postId)?.toPostContentModel()
    }

    override suspend fun clearHomeItems() {
        postDao.clearHomeItems()
    }
}