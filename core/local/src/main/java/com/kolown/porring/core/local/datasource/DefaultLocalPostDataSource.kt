package com.kolown.porring.core.local.datasource

import androidx.paging.PagingSource
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.local.dao.GalleryPostDao
import com.kolown.porring.core.local.dao.HomePostDao
import com.kolown.porring.core.local.dao.MyPostDao
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.dao.RandomPostDao
import com.kolown.porring.core.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultLocalPostDataSource @Inject constructor(
    private val postDao: PostDao,
    private val homePostDao: HomePostDao,
    private val randomPostDao: RandomPostDao,
    private val galleryPostDao: GalleryPostDao,
    private val myPostDao: MyPostDao
) : LocalPostDataSource {
    override suspend fun insertItems(
        items: List<OtherPostData>,
        itemType: PostsUsageType
    ) {
        when (itemType) {
            PostsUsageType.HOME -> homePostDao.insertHomePosts(items)
            PostsUsageType.RANDOM -> randomPostDao.insertRandomPosts(items)
            PostsUsageType.GALLERY -> galleryPostDao.insertGalleryPosts(items)
        }
        postDao.insertItems(items, itemType)
    }

    override suspend fun getFirstRandomItem() = randomPostDao.getFirstRandomItem()

    override suspend fun getLastRandomItem() = randomPostDao.getLastRandomItem()

    override suspend fun getLastGalleryItem() = galleryPostDao.getLastGalleryItem()

    override fun getHomePosts(): Flow<List<OtherPostData>> {
        return homePostDao.getHomePosts()
    }

    override fun getRandomPosts(): PagingSource<Int, OtherPostData> = randomPostDao.getRandomPosts()

    override fun getGalleryPosts(): PagingSource<Int, OtherPostData> =
        galleryPostDao.getGalleryPosts()

    override suspend fun getItemById(postId: String): OtherPostData? {
        return postDao.getItemById(postId)
    }

    override suspend fun clearHomePosts() {
        homePostDao.clearHomeItems()
    }

    override suspend fun clearRandomPosts() {
        randomPostDao.clearRandomItems()
    }

    override suspend fun clearGalleryPosts() {
        galleryPostDao.clearGalleryItems()
    }

    override fun getMyPosts(): PagingSource<Int, MyPostData> {
        return myPostDao.getMyPosts()
    }

    override suspend fun insertMyPost(posts: List<MyPostData>) {
        myPostDao.insertMyPost(posts.map { it.toEntity() })
    }

    override suspend fun deleteMyPost(postId: String) {
        myPostDao.deleteMyPost(postId)
    }

    override suspend fun clearMyPost() {
        myPostDao.clearMyPost()
    }
}