package com.kolown.porring.core.data.repository

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.datasource.paging.PagingDataSource.Companion.createPager
import com.kolown.porring.core.data.model.PostsUsageType.HOME
import com.kolown.porring.core.data.model.PostsUsageType.PAGING
import com.kolown.porring.core.data.model.toModel
import com.kolown.porring.core.data.model.toMyPost
import com.kolown.porring.core.data.model.toPostContentModel
import com.kolown.porring.core.data.remotemediator.RandomPostRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserDetailRemoteMediatorFactory
import com.kolown.porring.core.data.remotemediator.UserGalleryPostRemoteMediatorFactory
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.DETAIL_PER_PAGE
import com.kolown.porring.core.data.repository.PostRepositoryImpl.Companion.GALLERY_PAGE_SIZE
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.model.UploadFeedBack
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.model.toReactions
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.ImageDataSource
import com.kolown.porring.core.network.PostDataSource
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.TagDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    fun uploadPost(fileUri: Uri, description: String, tags: List<String>)

    fun getPostBySearch(tagId: String): Flow<PagingData<PostContentModel>>

    suspend fun getHomeItemPosts(): Flow<List<PostContentModel>>
    suspend fun fetchHomeItemPosts()

    suspend fun insertPagingItem(item: PostContentModel)
    suspend fun clearPagingItems()

    fun getPagingItemPosts(
        postType: PostType,
        pageState: StateFlow<PageState>?,
        authorId: String? = null,
        postId: String? = null
    ): Flow<PagingData<PostContentModel>>

    suspend fun updatePostReaction(postId: String, reaction: Reactions)

    fun getMyPosts(): Flow<PagingData<MyPost>>
    suspend fun fetchMyPosts()
    suspend fun deletePost(postId: String): Flow<Result<Unit>>
}

class PostRepositoryImpl @Inject constructor(
    private val imageDataSource: ImageDataSource,
    private val postDataSource: PostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource, // TODO: 삭제예정
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    private val localPostDataSource: LocalPostDataSource,
    private val followDataSource: FollowDataSource, // TODO: 삭제예정
    private val randomPostRemoteMediatorFactory: RandomPostRemoteMediatorFactory,
    private val userDetailRemoteMediatorFactory: UserDetailRemoteMediatorFactory,
    private val userGalleryRemoteMediatorFactory: UserGalleryPostRemoteMediatorFactory,
) : PostRepository {
    private val _uploadFeedBack = MutableSharedFlow<UploadFeedBack>()

    override fun getUploadFeedBack(): Flow<UploadFeedBack> = _uploadFeedBack.asSharedFlow()

    override fun uploadPost(
        fileUri: Uri,
        description: String,
        tags: List<String>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _uploadFeedBack.emit(UploadFeedBack.Uploading)

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
                _uploadFeedBack.emit(UploadFeedBack.Success)
            } catch (e: Exception) {
                _uploadFeedBack.emit(
                    UploadFeedBack.Error(
                        UploadModel(
                            fileUri.toString(),
                            description,
                            tags
                        )
                    )
                )
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
            pagingSourceFactory = { localPostDataSource.getPagingItems() }
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
            pagingSourceFactory = { localPostDataSource.getPagingItems() }
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
            throw IOException("게시물 불러오기 실패")
        }.map {
            PostContentModel(
                postId = it.postId,
                authorId = it.authorId,
                imageUrl = it.imageUrl,
                registerAt = it.registerAt,
                description = it.description,
                tags = it.tags,
                isFollower = it.isFollower,
                reactions = it.reactions.map { r -> r.toReactions() ?: Reactions.LOVE },
                myReaction = it.myReaction?.toReactions()
            )
        }

        localPostDataSource.clearHomeItems()
        delay(100)
        localPostDataSource.insertItems(
            posts,
            HOME
        )
    }

    override suspend fun updatePostReaction(postId: String, reaction: Reactions) {
        val myReaction = localPostDataSource.getMyReaction(postId)

        if (myReaction == reaction.value) {
            deletePostReaction(postId)
        } else {
            setPostReaction(postId, reaction)
        }
    }

    private suspend fun setPostReaction(postId: String, reaction: Reactions): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            localPostDataSource.setPostReaction(postId, reaction.value)
            postDataSource.setPostReaction(
                userId = currentUserId,
                postId = postId,
                reaction = reaction
            )
        }
    }

    private suspend fun deletePostReaction(postId: String): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = googleAuthDataSource.getUserId()

            localPostDataSource.deletePostReaction(postId)
            postDataSource.deletePostReaction(postId = postId, userId = currentUserId)
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

    override suspend fun fetchMyPosts() {
        val uid = googleAuthDataSource.getUserId()
        val posts = postDataSource.getAllPostsByAuthorId(uid).getOrThrow()

        localPostDataSource.insertMyPost(posts.map { it.toMyPost() })
    }

    override fun getPostBySearch(tagId: String): Flow<PagingData<PostContentModel>> {
        return createPager(
            pageSize = SEARCH_PER_PAGE,
            keySelector = { it.postId }
        ) { startKey, perPage ->
            fetchPostContentModelsByTagId(tagId, startKey, perPage)
        }
    }

    override suspend fun deletePost(postId: String): Flow<Result<Unit>> {
        localPostDataSource.deleteMyPost(postId)

        return postDataSource.deletePost(postId)
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
        localPostDataSource.insertItems(
            listOf(item),
            PAGING
        )
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

    private suspend fun fetchPostContentModelsByTagId(
        tagId: String,
        startKey: String?,
        perPage: Int
    ): Result<List<PostContentModel>> = runCatching {
        val currentUserId = googleAuthDataSource.getUserId()

        val postIds = tagDataSource.getPostTagByTagId(tagId).getOrElse {
            throw IOException("포스트 ID 가져오기 실패")
        }

        val posts = postDataSource.getPostBySearch(
            currentUserId = currentUserId,
            postIds = postIds,
            key = startKey,
            perPage = perPage.toLong()
        ).getOrElse {
            throw IOException("포스트 가져오기 실패")
        }

        val (tags, reactions, isFollowers) = coroutineScope {
            val tagsDeferred = async {
                posts.map {
                    async {
                        tagDataSource.getPostTag(it.postId).getOrElse { emptyList() }
                    }
                }.awaitAll()
            }

            val reactionsDeferred = async {
                posts.map {
                    async {
                        reactionDataSource.getReactionByPostId(it.postId).getOrElse { emptyList() }
                    }
                }.awaitAll()
            }

            val followDeferred = async {
                posts.map {
                    async {
                        followDataSource.getIsFollower(currentUserId, it.authorId)
                            .getOrElse { false }
                    }
                }.awaitAll()
            }

            Triple(tagsDeferred.await(), reactionsDeferred.await(), followDeferred.await())
        }

        posts.mapIndexed { index, postModel ->
            PostContentModel(
                postId = postModel.postId,
                authorId = postModel.authorId,
                imageUrl = postModel.imageUrl,
                registerAt = postModel.registerAt,
                description = postModel.description,
                tags = tags[index].map { it.tagName },
                isFollower = isFollowers[index],
                reactions = reactions[index].mapNotNull { it.reaction },
                myReaction = reactions[index].find { it.userId == currentUserId }?.reaction
            )
        }
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
