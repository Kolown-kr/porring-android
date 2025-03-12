package com.kolown.porring.core.data.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.kolown.porring.core.local.LocalPostDataSource
import com.kolown.porring.core.local.room.dao.ItemType
import com.kolown.porring.core.local.room.dto.PostData
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.TagDataSource
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Named

@OptIn(ExperimentalPagingApi::class)
class UserPostRemoteMediator @AssistedInject constructor(
    @Assisted private val postItem: PostContentModel,
    @Assisted private val pageState: StateFlow<PageState>,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    @Named("local_post_datasource") private val localPostDataSource: LocalPostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource,
    private val followDataSource: FollowDataSource,
) : RemoteMediator<Int, PostData>() {
    private var isLoading = false

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostData>,
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                val pageSize = state.config.pageSize.toLong()

                if (loadType == LoadType.REFRESH) onRefresh(pageSize)

                pageState.collectLatest { pageState ->
                    if (isLoading.not() && pageState.currentPage < pageSize) {
                        isLoading = true
                        launch {
                            try {
                                onPrepend(pageSize)
                            } finally {
                                isLoading = false
                            }
                        }
                    }

                    if (isLoading.not() && pageState.pageCount - pageSize - 1 < pageState.currentPage) {
                        isLoading = true
                        launch {
                            try {
                                onAppend(pageSize)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
                return@withContext MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Log.e("UserPostRemoteMediator: fatal", "error: $e")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun onRefresh(pageSize: Long) {
        val result = coroutineScope {
            val prev = async {
                Firebase.firestore.collection("post")
                    .whereEqualTo("authorId", postItem.authorId)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .endBefore(postItem.registerAt)
                    .limitToLast(pageSize)
                    .get()
                    .await()
                    .toObjects(PostDto::class.java)
                    .map { it.toPostModel() }
                    .getPostContent()
            }
            val next = async {
                Firebase.firestore.collection("post")
                    .whereEqualTo("authorId", postItem.authorId)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .startAfter(postItem.registerAt)
                    .limit(pageSize)
                    .get()
                    .await()
                    .toObjects(PostDto::class.java)
                    .map { it.toPostModel() }
                    .getPostContent()
            }
            prev.await() + next.await()
        }

        localPostDataSource.insertItems(result, ItemType.PAGING_ITEM)
    }

    private suspend fun onPrepend(pageSize: Long) {
        val firstItem = localPostDataSource.getFirstPageItem()

        val result = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", postItem.authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .endBefore(firstItem.registerAt)
            .limitToLast(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }
            .getPostContent()

        localPostDataSource.insertItems(result, ItemType.PAGING_ITEM)
    }

    private suspend fun onAppend(pageSize: Long) {
        val lastItem = localPostDataSource.getLastPageItem()

        val result = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", postItem.authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .startAfter(lastItem.registerAt)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }
            .getPostContent()

        localPostDataSource.insertItems(result, ItemType.PAGING_ITEM)
    }

    private suspend fun List<PostModel>.getPostContent(): List<PostContentModel> {
        val currentUserId = googleAuthDataSource.getUserId()
        val posts = this
        val (tags, reactions, isFollowers) = coroutineScope {
            val tagsDeferred = async {
                posts.map {
                    async {
                        tagDataSource.getPostTag(it.postId).getOrElse {
                            throw IOException("태그 불러오기 실패")
                        }
                    }
                }.awaitAll()
            }
            val reactionsDeferred = async {
                posts.map {
                    async {
                        reactionDataSource.getReactionByPostId(it.postId).getOrElse {
                            throw IOException("리액션 불러오기 실패")
                        }
                    }
                }.awaitAll()
            }
            val followDeferred = async {
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

            Triple(tagsDeferred.await(), reactionsDeferred.await(), followDeferred.await())
        }

        return posts.mapIndexed { index, postModel ->
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
}