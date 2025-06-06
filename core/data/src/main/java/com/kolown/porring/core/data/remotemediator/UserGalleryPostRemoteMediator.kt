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
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.PostUiModel
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Named

@OptIn(ExperimentalPagingApi::class)
class UserGalleryPostRemoteMediator @AssistedInject constructor(
    @Assisted("author_id") private val authorId: String,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    private val localPostDataSource: LocalPostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource,
    private val followDataSource: FollowDataSource,
) : RemoteMediator<Int, com.kolown.porring.core.data.model.OtherPostData>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, com.kolown.porring.core.data.model.OtherPostData>
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                when (loadType) {
                    LoadType.REFRESH -> onRefresh(state.config.pageSize.toLong())
                    LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> onAppend(state.config.pageSize.toLong())
                }
            }
        } catch (e: Exception) {
            Log.e("GallryPostRemoteMediator: fatal", "error: $e")
            MediatorResult.Error(e)
        }

    }

    private suspend fun onRefresh(pageSize: Long): MediatorResult {
        val posts = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }
            .getPostContent()

        localPostDataSource.insertItems(
            posts,
            com.kolown.porring.core.data.model.PostsUsageType.PAGING
        )

        return MediatorResult.Success(endOfPaginationReached = posts.isEmpty())
    }

    private suspend fun onAppend(pageSize: Long): MediatorResult {
        val lastItem = localPostDataSource.getLastPageItem()

        val result = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .startAfter(lastItem.registerAt)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }
            .getPostContent()

        localPostDataSource.insertItems(
            result,
            com.kolown.porring.core.data.model.PostsUsageType.PAGING
        )

        return MediatorResult.Success(endOfPaginationReached = result.isEmpty())
    }

    private suspend fun List<PostModel>.getPostContent(): List<PostUiModel> {
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
            PostUiModel(
                postId = postModel.postId,
                authorId = postModel.authorId,
                imageUrl = postModel.imageUrl,
                registerAt = postModel.registerAt,
                description = postModel.description,
                tags = tags[index].map { it.tagName },
                isFollowing = isFollowers[index],
                reactions = reactions[index].mapNotNull { it.reaction },
                myReaction = reactions[index].find { it.userId == currentUserId }?.reaction
            )
        }
    }
}