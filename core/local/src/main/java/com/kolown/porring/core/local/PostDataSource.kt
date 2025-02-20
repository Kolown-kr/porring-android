package com.kolown.porring.core.local

import com.kolown.porring.core.local.room.dao.HomePostDao
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
}

class HomePostDataSourceImpl @Inject constructor(
    private val homePostDao: HomePostDao
) : LocalPostDataSource {
    override suspend fun insertItems(items: List<PostContentModel>) {
        homePostDao.insertItems(items.map { it.toPostDto() })
    }

    override fun getItems(): Flow<List<PostContentModel>> {
        return homePostDao.getItems().map { items -> items.map { it.toPostContentModel() } }
    }

    override suspend fun updateReaction(postId: String, reaction: Int) {
        val post = homePostDao.getItemById(postId) ?: return
        val myReaction = if (post.myReaction == reaction) null else reaction
        val reactions = post.reactions.toMutableList()

        reactions.remove(post.myReaction)
        if (myReaction != null) reactions.add(reaction)

        homePostDao.updateReactions(postId, reactions.toList())
        homePostDao.updateMyReaction(postId, myReaction)
    }

    override suspend fun getItemById(postId: String): PostContentModel? {
        return homePostDao.getItemById(postId)?.toPostContentModel()
    }

    override suspend fun clearHomeItems() {
        homePostDao.clearHomeItems()
    }
}