package com.kolown.porring.core.data.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.model.LocalPostDto
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.TagDataSource
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
class RandomDetailPostRemoteMediator @AssistedInject constructor(
    @Assisted private val pageState: StateFlow<PageState>,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    @Named("local_post_datasource") private val localPostDataSource: LocalPostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource,
    private val followDataSource: FollowDataSource,
) : RemoteMediator<Int, LocalPostDto>() {
    private var isLoading = false
    private var nextKey = 0L
    private val currentUserId = googleAuthDataSource.getUserId()
    private var randomType = "ABCDE".random().toString()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalPostDto>
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                val pageSize = state.config.pageSize.toLong()

                if (loadType == LoadType.REFRESH) {
                    nextKey = (0..Long.MAX_VALUE).random()
                    getPosts(nextKey, pageSize)
                }

                if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(
                    endOfPaginationReached = true
                )

                pageState.collectLatest { pageState ->
                    if (isLoading.not() && pageState.pageCount - state.config.pageSize - 1 < pageState.currentPage) {
                        isLoading = true
                        launch {
                            try {
                                getPosts(nextKey, pageSize)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }

                return@withContext MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Log.e("PostRemoteMediator: fatal", "error: $e")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getPosts(key: Long, pageSize: Long) {
        val query: suspend (Long, Long) -> Query = { seed, pageSize ->
            Firebase.firestore.collection("post")
                .whereNotEqualTo("authorId", currentUserId)
                .whereGreaterThan("random$randomType", seed)
                .orderBy("random$randomType", Query.Direction.ASCENDING)
                .orderBy("postId")
                .limit(pageSize)
        }
        val results = coroutineScope {
            val posts = async {
                query(key, pageSize)
                    .get()
                    .await()
                    .toObjects(com.kolown.porring.core.network.model.PostDto::class.java)
                    .map { it.toPostModel(randomType) }
            }.await()

            if (posts.size < pageSize) {
                val additions = async {
                    query(0, pageSize - posts.size)
                        .get()
                        .await()
                        .toObjects(com.kolown.porring.core.network.model.PostDto::class.java)
                        .map { it.toPostModel(randomType) }
                }.await()

                posts + additions
            } else {
                posts
            }
        }

        localPostDataSource.insertItems(
            results.getPostContent(),
            com.kolown.porring.core.data.model.LocalItemType.PAGING_ITEM
        )

        val currentLastKey = results.lastOrNull()?.random ?: nextKey

        nextKey = currentLastKey + 1
    }

    private suspend fun List<PostModel>.getPostContent(): List<PostContentModel> {
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