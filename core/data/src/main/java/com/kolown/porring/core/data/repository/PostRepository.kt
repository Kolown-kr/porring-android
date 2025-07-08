package com.kolown.porring.core.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.api.datasource.local.LocalUserCacheDataSource
import com.kolown.porring.core.data.datasource.paging.PagingDataSource.Companion.createPager
import com.kolown.porring.core.data.mapper.toData
import com.kolown.porring.core.data.mapper.toModel
import com.kolown.porring.core.data.mapper.toMyData
import com.kolown.porring.core.data.mapper.toOtherData
import com.kolown.porring.core.data.model.PostsUsageType.HOME
import com.kolown.porring.core.data.model.PostsUsageType.PAGING
import com.kolown.porring.core.data.remotemediator.RandomPostRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserDetailRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserGalleryPostRemoteMediatorFactory
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.DETAIL_PER_PAGE
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.GALLERY_PAGE_SIZE
import com.kolown.porring.core.data.utils.retryWithLimit
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.ReactedPost
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.model.UploadFeedBack
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.PorringDateTime.getNowDateTimeUTCString
import com.kolown.porring.core.network.PostDataSource
import com.kolown.porring.core.network.TagDataSource
import com.kolown.porring.core.network.UserDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

interface PostRepository {
    fun getUploadFeedBack(): Flow<UploadFeedBack>
    fun uploadPost(fileUrl: String, uploadModel: UploadModel)

    fun getPostBySearch(tagName: String): Flow<PagingData<PostModel>>

    suspend fun getHomeItemPosts(): Flow<List<PostModel>>
    suspend fun fetchHomeItemPosts()

    suspend fun insertPagingItem(item: PostModel)
    suspend fun clearPagingItems()
    fun getPagingItemPosts(
        postType: PostType,
        pageState: StateFlow<PageState>?,
        authorId: String? = null,
        postId: String? = null
    ): Flow<PagingData<PostModel>>

    suspend fun updatePostReaction(postId: String, reaction: Reaction)

    fun getMyPosts(): Flow<PagingData<MyPost>>
    suspend fun fetchMyPosts()
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun clearMyPostCache()
}

class PostRepositoryImpl @Inject constructor(
    private val postDataSource: PostDataSource,
    private val tagDataSource: TagDataSource,
    private val userDataSource: UserDataSource, // TODO: domain분리 후 useCase로 이관하며 삭제해야함
    private val localUserCacheDataSource: LocalUserCacheDataSource, // TODO: domain분리 후 useCase로 이관하며 삭제해야함
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    private val localPostDataSource: LocalPostDataSource,
    private val randomPostRemoteMediatorFactory: RandomPostRemoteMediatorFactory,
    private val userDetailRemoteMediatorFactory: UserDetailRemoteMediatorFactory,
    private val userGalleryRemoteMediatorFactory: UserGalleryPostRemoteMediatorFactory,
) : PostRepository {
    private val _uploadFeedBack = MutableSharedFlow<UploadFeedBack>()

    override fun getUploadFeedBack(): Flow<UploadFeedBack> = _uploadFeedBack.asSharedFlow()

    override fun uploadPost(
        fileUrl: String,
        uploadModel: UploadModel
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _uploadFeedBack.emit(UploadFeedBack.Uploading)

            val authorId = googleAuthDataSource.getUserId()
            val failureModel = uploadModel.copy(imgUri = fileUrl)

            retryWithLimit {
                postDataSource.uploadPost(
                    authorId = authorId,
                    description = uploadModel.description,
                    imageUrl = uploadModel.imgUri,
                    imageRatio = uploadModel.imageRatio,
                    tags = uploadModel.categoryItems
                )
            }.onSuccess { postId ->
                retryWithLimit {
                    tagDataSource.uploadTags(uploadModel.categoryItems, postId)
                }.onSuccess {
                    _uploadFeedBack.emit(UploadFeedBack.Success)
                }.onFailure {
                    Log.e("uploadPost", it.message.toString())
                    postDataSource.deletePost(postId)
                    _uploadFeedBack.emit(UploadFeedBack.Error(failureModel))
                }
            }.onFailure {
                Log.e("uploadPost", it.message.toString())
                _uploadFeedBack.emit(UploadFeedBack.Error(failureModel))
            }
        }
    }

    override suspend fun getHomeItemPosts(): Flow<List<PostModel>> =
        withContext(Dispatchers.IO) {
            localPostDataSource.getItems().map { data -> data.map { it.toModel() } }
        }

    override suspend fun clearPagingItems() = withContext(Dispatchers.IO) {
        localPostDataSource.clearPagingItems()
    }

    override fun getPagingItemPosts(
        postType: PostType,
        pageState: StateFlow<PageState>?,
        authorId: String?,
        postId: String?
    ): Flow<PagingData<PostModel>> {
        return when (postType) {
            PostType.RANDOM_DETAIL -> getRandomPagingItem(
                postType.pageSize,
                pageState,
            )

            PostType.USER_DETAIL -> getUserDetailPagingItem(
                postType.pageSize,
                pageState,
                authorId,
                postId
            )

            PostType.USER_GALLERY -> getUserGalleryPagingItem(
                postType.pageSize,
                authorId,
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getRandomPagingItem(
        pageSize: Int,
        pageState: StateFlow<PageState>?
    ): Flow<PagingData<PostModel>> {
        if (pageState == null) throw IllegalArgumentException("Page State가 없습니다.")

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = true,
            ),
            remoteMediator = randomPostRemoteMediatorFactory.create(pageState),
            pagingSourceFactory = { localPostDataSource.getPagingItems() }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.toModel()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getUserDetailPagingItem(
        pageSize: Int,
        pageState: StateFlow<PageState>?,
        authorId: String?,
        postId: String?
    ): Flow<PagingData<PostModel>> {
        if (pageState == null) throw IllegalArgumentException("Page State가 없습니다.")
        if (authorId == null) throw IllegalArgumentException("Author Id가 없습니다.")

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = true,
            ),
            remoteMediator = userDetailRemoteMediatorFactory.create(
                authorId = authorId,
                postId = postId,
                pageState = pageState
            ),
            pagingSourceFactory = { localPostDataSource.getPagingItems() }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.toModel()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getUserGalleryPagingItem(
        pageSize: Int,
        authorId: String?,
    ): Flow<PagingData<PostModel>> {
        if (authorId == null) throw IllegalArgumentException("Author Id가 없습니다.")

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
            ),
            remoteMediator = userGalleryRemoteMediatorFactory.create(authorId = authorId),
            pagingSourceFactory = { localPostDataSource.getPagingItems() }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.toModel()
            }
        }
    }

    override suspend fun fetchHomeItemPosts() = withContext(Dispatchers.IO) {
        val currentUserId = googleAuthDataSource.getUserId()

        val posts = postDataSource.fetchRandomPost(
            currentUserId,
            HOME_ITEM_SIZE,
            RANDOM_SEED.random().toString()
        ).getOrElse {
            throw IOException("게시물 불러오기 실패")
        }

        localPostDataSource.clearHomeItems()
        delay(100)
        localPostDataSource.insertItems(
            posts.map { it.toOtherData() },
            HOME
        )
    }

    // TODO: domain생성후 useCase로 reactedPost를 유저 컬렉션에 등록하는 로직 이동해야함.
    override suspend fun updatePostReaction(postId: String, reaction: Reaction) {
        val myReaction = localUserCacheDataSource.getMyReaction(postId)
        val currentTime = getNowDateTimeUTCString()
        val reactedPost =
            ReactedPost(postId = postId, reaction = reaction.value, registerAt = currentTime)

        if (myReaction == reaction.value) {
            deletePostReaction(reactedPost)
        } else {
            setPostReaction(reactedPost, myReaction)
        }
    }

    private suspend fun setPostReaction(
        reactedPost: ReactedPost,
        prevReaction: Int?
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            userDataSource.setReactedPost(
                userId = currentUserId,
                reactedPost = reactedPost
            )
            localUserCacheDataSource.insertReactedPosts(listOf(reactedPost.toData()))
            postDataSource.setPostReaction(
                postId = reactedPost.postId,
                reaction = reactedPost.reaction,
                prevReaction = prevReaction
            )
        }
    }

    private suspend fun deletePostReaction(reactedPost: ReactedPost): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            userDataSource.deleteReactedPost(
                userId = currentUserId,
                postId = reactedPost.postId
            )
            localUserCacheDataSource.deleteReactedPost(reactedPost.postId)
            postDataSource.deletePostReaction(
                postId = reactedPost.postId,
                reaction = reactedPost.reaction
            )
        }
    }

    override fun getMyPosts(): Flow<PagingData<MyPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = GALLERY_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { localPostDataSource.getMyPosts() }
        ).flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    override suspend fun fetchMyPosts() = withContext(Dispatchers.IO) {
        val uid = googleAuthDataSource.getUserId()
        val posts = postDataSource.getAllPostsByAuthorId(uid).getOrThrow()

        localPostDataSource.insertMyPost(posts.map { it.toMyData() })
    }

    override fun getPostBySearch(tagName: String): Flow<PagingData<PostModel>> {
        return createPager(
            pageSize = SEARCH_PER_PAGE,
            keySelector = { it.postId }
        ) { startKey, perPage ->
            fetchPostContentModelsByTagName(tagName, startKey, perPage)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        localPostDataSource.deleteMyPost(postId)

        return postDataSource.deletePost(postId)
    }

    override suspend fun clearMyPostCache() {
        localPostDataSource.clearMyPost()
    }

    override suspend fun insertPagingItem(item: PostModel) {
        localPostDataSource.insertItems(
            listOf(item.toOtherData()),
            PAGING
        )
    }

    private suspend fun fetchPostContentModelsByTagName(
        tagName: String,
        startKey: String?,
        perPage: Int
    ): Result<List<PostModel>> {
        val currentUserId = googleAuthDataSource.getUserId()
        val postIds = tagDataSource.getPostIdsByTagName(tagName).getOrElse {
            return Result.failure(it)
        }

        if (postIds.isEmpty()) return Result.success(emptyList())

        val posts = postDataSource.getPostBySearch(
            currentUserId = currentUserId,
            postIds = postIds,
            key = startKey,
            perPage = perPage.toLong()
        ).getOrElse {
            return Result.failure(it)
        }

        return Result.success(posts)
    }

    companion object {
        const val DETAIL_PER_PAGE = 5
        const val SEARCH_PER_PAGE = 5
        const val GALLERY_PAGE_SIZE = 15
        const val HOME_ITEM_SIZE = 10
        const val RANDOM_SEED = "ABCDE"
    }
}

enum class PostType(val pageSize: Int) {
    RANDOM_DETAIL(DETAIL_PER_PAGE),
    USER_DETAIL(DETAIL_PER_PAGE),
    USER_GALLERY(GALLERY_PAGE_SIZE),
}
