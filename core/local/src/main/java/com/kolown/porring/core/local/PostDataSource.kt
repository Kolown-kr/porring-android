package com.kolown.porring.core.local

import com.kolown.porring.core.local.room.dao.HomePostDao
import com.kolown.porring.core.local.room.dto.PostDto
import com.kolown.porring.core.local.room.entity.PostType
import com.kolown.porring.core.model.PostContentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LocalPostDataSource {
    fun getItems(): Flow<List<PostContentModel>>
    suspend fun getItemById(postId: String): PostContentModel?
    suspend fun insertItems(items: List<PostContentModel>)
    suspend fun updateItem(item: PostContentModel)
    suspend fun clearAllItems()
}

class HomePostDataSourceImpl @Inject constructor(
    private val homePostDao: HomePostDao
) : LocalPostDataSource {
    override fun getItems(): Flow<List<PostContentModel>> {
        return homePostDao.getItems().map { items -> items.map { it.toPostContentModel() } }
    }

    override suspend fun getItemById(postId: String): PostContentModel? {
        return homePostDao.getItemById(postId)?.toPostContentModel()
    }

    override suspend fun insertItems(items: List<PostContentModel>) {
        homePostDao.insertItems(items.map { it.toPostDto(PostType.HOME_ITEM_POST) as PostDto.HomeItemPostDto })
    }

    override suspend fun updateItem(item: PostContentModel) {
        homePostDao.updateItem(item.toPostDto(PostType.HOME_ITEM_POST) as PostDto.HomeItemPostDto)
    }

    override suspend fun clearAllItems() {
        homePostDao.clearAllItems()
    }
}