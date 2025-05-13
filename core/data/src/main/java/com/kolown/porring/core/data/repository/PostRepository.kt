package com.kolown.porring.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kolown.porring.core.data.datasource.paging.RandomPagingDataSource
import com.kolown.porring.core.data.datasource.paging.SearchPagingSource
import com.kolown.porring.core.data.datasource.paging.UserPagingDataSource
import com.kolown.porring.core.data.datasource.paging.UserPagingKey
import com.kolown.porring.core.data.remotemediator.RandomPostRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserDetailRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserGalleryPostRemoteMediatorFactory
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.DETAIL_PER_PAGE
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.GALLERY_PAGE_SIZE
import com.kolown.porring.core.local.LocalPostDataSource
import com.kolown.porring.core.local.room.dao.ItemType
import com.kolown.porring.core.local.toPostContentModel
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.ImageDataSource
import com.kolown.porring.core.network.PostDataSource
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.TagDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

interface PostRepository {
    suspend fun uploadPost(fileUri: Uri, description: String, tags: List<String>): Result<Unit>
    fun getRandomDetailPostList(): Flow<PagingData<PostContentModel>>
    suspend fun reactPost(postId: String, reaction: Reactions): Result<Unit>
    suspend fun removePostReaction(postId: String, reaction: Reactions): Result<Unit>
    fun getUserPosts(userId: String? = null): Flow<PagingData<PostContentModel>>
    fun getPostBySearch(tagId: String): Flow<PagingData<PostContentModel>>
    suspend fun deletePost(postId: String): Flow<Boolean>
    suspend fun getHomeItemPosts(): Flow<List<PostContentModel>>
    suspend fun fetchHomeItemPosts()
    suspend fun updateFollowState(authorId: String, isFollow: Boolean)
    suspend fun insertPagingItem(item: PostContentModel)
    suspend fun clearPagingItems()
    fun getPagingItemPosts(
        postType: PostType,
        pageState: StateFlow<PageState>?,
        authorId: String? = null,
        postId: String? = null
    ): Flow<PagingData<PostContentModel>>
}

class PostRepositoryImpl @Inject constructor(
    private val imageDataSource: ImageDataSource,
    private val postDataSource: PostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    @Named("local_post_datasource") private val localPostDataSource: LocalPostDataSource,
    private val followDataSource: FollowDataSource,
    private val randomPostRemoteMediatorFactory: RandomPostRemoteMediatorFactory,
    private val userDetailRemoteMediatorFactory: UserDetailRemoteMediatorFactory,
    private val userGalleryRemoteMediatorFactory: UserGalleryPostRemoteMediatorFactory,
) : PostRepository {
    override fun getUserPosts(userId: String?): Flow<PagingData<PostContentModel>> {
        val authorId = googleAuthDataSource.getUserId()
        val initialKey = if (userId == null) {
            UserPagingKey(0, authorId)
        } else {
            UserPagingKey(0, userId)
        }

        return Pager(
            config = PagingConfig(
                pageSize = GALLERY_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            initialKey = initialKey,
            pagingSourceFactory = {
                UserPagingDataSource(
                    postDataSource = postDataSource,
                    tagDataSource = tagDataSource,
                    reactionDataSource = reactionDataSource,
                    googleAuthDataSource = googleAuthDataSource,
                    userId = userId ?: authorId
                )
            }
        ).flow
    }

    override suspend fun uploadPost(
        fileUri: Uri,
        description: String,
        tags: List<String>,
    ): Result<Unit> {
        return runCatching {
            coroutineScope {
                val authorId = googleAuthDataSource.getUserId()

                val postIdDeferred = async {
                    retryWithLimit {
                        postDataSource.uploadPost(authorId, description).getOrElse {
                            throw IOException("게시물 업로드 실패")
                        }
                    }.getOrThrow()
                }
                val tagIdsDeferred = async {
                    retryWithLimit {
                        tagDataSource.uploadTags(tags).getOrElse {
                            throw IOException("태그 업로드 실패")
                        }
                    }.getOrThrow()
                }

                val (postId, tagIds) = postIdDeferred.await() to tagIdsDeferred.await()

                launch {
                    retryWithLimit {
                        tagDataSource.uploadPostTags(tagIds, postId).getOrElse {
                            throw IOException("포스트 태그 업로드 실패")
                        }
                    }.getOrThrow()
                }

                launch {
                    retryWithLimit {
                        updateImageUrl(postId, fileUri).getOrElse {
                            throw IOException("이미지 uri 업로드 실패")
                        }
                    }.getOrThrow()
                }
            }
        }
    }

    override suspend fun getHomeItemPosts(): Flow<List<PostContentModel>> =
        withContext(Dispatchers.IO) { localPostDataSource.getItems() }

    override suspend fun clearPagingItems() = withContext(Dispatchers.IO) {
        localPostDataSource.clearPagingItems()
    }

    override fun getPagingItemPosts(
        postType: PostType,
        pageState: StateFlow<PageState>?,
        authorId: String?,
        postId: String?
    ): Flow<PagingData<PostContentModel>> {
        return when (postType) {
            PostType.RANDOM_DETAIL -> getRandomPagingItem(postType.pageSize, pageState)
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
    ): Flow<PagingData<PostContentModel>> {
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
                data.toPostContentModel()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getUserDetailPagingItem(
        pageSize: Int,
        pageState: StateFlow<PageState>?,
        authorId: String?,
        postId: String?
    ): Flow<PagingData<PostContentModel>> {
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
            pagingSourceFactory = { localPostDataSource.getPagingItems(true) }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.toPostContentModel()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getUserGalleryPagingItem(
        pageSize: Int,
        authorId: String?,
    ): Flow<PagingData<PostContentModel>> {
        if (authorId == null) throw IllegalArgumentException("Author Id가 없습니다.")

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
            ),
            remoteMediator = userGalleryRemoteMediatorFactory.create(authorId = authorId),
            pagingSourceFactory = { localPostDataSource.getPagingItems(true) }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.toPostContentModel()
            }
        }
    }

    override suspend fun fetchHomeItemPosts() = withContext(Dispatchers.IO) {
        val currentUserId = googleAuthDataSource.getUserId()

        val posts = postDataSource.getRandomPost(
            currentUserId,
            HOME_ITEM_SIZE,
            RANDOM_SEED.random().toString()
        ).getOrElse {
            Log.e("fatal: postRepository", it.toString())
            throw IOException("게시물 불러오기 실패")
        }
        val isFollowers = coroutineScope {
            posts.map {
                async {
                    followDataSource.getIsFollower(
                        userId = currentUserId,
                        followerId = it.authorId
                    ).getOrElse {
                        throw IOException("팔로우 확인 실패")
                    }
                }
            }.awaitAll()
        }

        val postContentModels = posts.mapIndexed { index, postModel ->
            PostContentModel(
                postId = postModel.postId,
                authorId = postModel.authorId,
                imageUrl = postModel.imageUrl,
                registerAt = postModel.registerAt,
                description = postModel.description,
                tags = postModel.tags,
                isFollower = isFollowers[index],
                reactions = postModel.reactions,
                myReaction = postModel.myReaction
            )
        }

        localPostDataSource.clearHomeItems()
        delay(100)
        localPostDataSource.insertItems(postContentModels, ItemType.HOME_ITEM)
    }

    override fun getRandomDetailPostList(): Flow<PagingData<PostContentModel>> {
        return Pager(
            config = PagingConfig(pageSize = DETAIL_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = {
                RandomPagingDataSource(
                    postDataSource,
                    tagDataSource,
                    reactionDataSource,
                    followDataSource,
                    googleAuthDataSource
                )
            }
        ).flow
    }

    override suspend fun updateFollowState(authorId: String, isFollow: Boolean) {
        localPostDataSource.updateFollowState(authorId, isFollow)
    }

    override suspend fun reactPost(postId: String, reaction: Reactions): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            localPostDataSource.updateReaction(postId, reaction.value)
            reactionDataSource.updatePostReaction(
                userId = currentUserId,
                postId = postId,
                reaction = reaction
            )
        }
    }

    override suspend fun removePostReaction(postId: String, reaction: Reactions): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            localPostDataSource.updateReaction(postId, reaction.value)
            reactionDataSource.removePostReaction(
                userId = currentUserId,
                postId = postId,
            )
        }
    }

    override fun getPostBySearch(tagId: String): Flow<PagingData<PostContentModel>> {
        return Pager(
            config = PagingConfig(pageSize = SEARCH_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchPagingSource(
                    postDataSource = postDataSource,
                    reactionDataSource = reactionDataSource,
                    tagId = tagId,
                    tagDataSource = tagDataSource,
                    followerDataSource = followDataSource,
                    googleAuthDataSource = googleAuthDataSource
                )
            }
        ).flow
    }

    override suspend fun deletePost(postId: String): Flow<Boolean> = flow {
        coroutineScope {
            val deletePostTagDeferred = async {
                retryWithLimit {
                    tagDataSource.deletePostTag(postId).getOrElse {
                        throw IOException("포스트 태그 삭제 실패")
                    }
                }
            }
            val deleteReactionDeferred = async {
                retryWithLimit {
                    reactionDataSource.deletePostReaction(postId).getOrElse {
                        throw IOException("리액션 삭제 실패")
                    }
                }
            }
            val deletePostDeferred = async {
                retryWithLimit {
                    postDataSource.deletePost(postId).getOrElse {
                        throw IOException("게시물 삭제 실패")
                    }
                }
            }

            val results = awaitAll(
                deletePostTagDeferred,
                deleteReactionDeferred,
                deletePostDeferred
            )

            val allSuccess = results.all { it.isSuccess }

            if (allSuccess) {
                emit(true)
            } else {
                throw IOException("하나 이상의 작업이 실패하였습니다.")
            }
        }
    }

    private suspend fun updateImageUrl(postId: String, fileUri: Uri): Result<Unit> {
        return runCatching {
            val authorId = googleAuthDataSource.getUserId()

            val documentId = postId.substringAfter("-")
            val imageUrl = imageDataSource.getImageUrl(authorId, fileUri).getOrElse {
                throw IOException("이미지 업로드 실패")
            }
            postDataSource.updateImageUrl(documentId, imageUrl)
        }
    }

    override suspend fun insertPagingItem(item: PostContentModel) {
        localPostDataSource.insertItems(listOf(item), ItemType.PAGING_ITEM)
    }

    private suspend fun <T> retryWithLimit(
        maxAttempts: Int = 3,
        delayMillis: Long = 1000,
        block: suspend () -> T
    ): Result<T> {
        repeat(maxAttempts - 1) { attempt ->
            try {
                return Result.success(block())
            } catch (e: Exception) {
                if (attempt < maxAttempts - 1) {
                    delay(delayMillis)
                }
            }
        }
        return runCatching { block() }
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
