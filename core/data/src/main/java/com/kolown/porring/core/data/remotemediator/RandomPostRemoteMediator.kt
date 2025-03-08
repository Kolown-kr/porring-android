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
import com.kolown.porring.core.local.room.dao.RemoteKeyDao
import com.kolown.porring.core.local.room.dto.PostData
import com.kolown.porring.core.local.room.entity.RemoteKey
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.TagDataSource
import com.kolown.porring.core.network.model.toPostModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalPagingApi::class)
class RandomPostRemoteMediator @Inject constructor(
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    @Named("local_post_datasource") private val localPostDataSource: LocalPostDataSource,
    private val tagDataSource: TagDataSource,
    private val reactionDataSource: ReactionDataSource,
    private val followDataSource: FollowDataSource,
    private val remoteKeyDao: RemoteKeyDao,
) : RemoteMediator<Int, PostData>() {

    private val currentUserId = googleAuthDataSource.getUserId()
    private var randomType = "ABCDE".random().toString()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostData>
    ): MediatorResult {
        return try {
            val (currentKey, loadSize) = when (loadType) {
                LoadType.REFRESH -> (0..Long.MAX_VALUE).random() to state.config.initialLoadSize
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> (remoteKeyDao.remoteKeysById("posts")?.nextKey
                    ?: 0) to state.config.pageSize
            }
            val query: suspend (Long, Long) -> Query = { seed, pageSize ->
                Firebase.firestore.collection("post")
                    .whereNotEqualTo("authorId", currentUserId)
                    .whereGreaterThan("random$randomType", seed)
                    .orderBy("random$randomType", Query.Direction.ASCENDING)
                    .orderBy("postId")
                    .limit(pageSize)
            }

            if (loadType == LoadType.REFRESH) remoteKeyDao.clearRemoteKeys()

            val results = coroutineScope {
                val posts = async {
                    query(currentKey, loadSize.toLong())
                        .get()
                        .await()
                        .toObjects(com.kolown.porring.core.network.model.PostDto::class.java)
                        .map { it.toPostModel(randomType) }
                }.await()

                if (posts.size < loadSize) {
                    val additions = async {
                        query(0, loadSize.toLong() - posts.size)
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

            if (results.isEmpty()) return MediatorResult.Success(endOfPaginationReached = true)

            localPostDataSource.insertItems(results.getPostContent(), ItemType.PAGING_ITEM)
            remoteKeyDao.insertOrReplace(
                RemoteKey(
                    prevKey = currentKey,
                    nextKey = results.last().random + 1
                )
            )

            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            Log.e("PostRemoteMediator: fatal", "error: $e")
            MediatorResult.Error(e)
        }
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