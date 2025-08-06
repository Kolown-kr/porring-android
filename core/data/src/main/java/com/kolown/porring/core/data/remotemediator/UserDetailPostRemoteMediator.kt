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
import com.kolown.porring.core.data.mapper.toOtherData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class UserDetailPostRemoteMediator @AssistedInject constructor(
    @Assisted("author_id") private val authorId: String,
    @Assisted("post_id") private val postId: String?,
    @Assisted private val pageState: StateFlow<PageState>,
    private val localPostDataSource: LocalPostDataSource,
) : RemoteMediator<Int, OtherPostData>() {
    private var isLoading = false

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OtherPostData>,
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                val pageSize = state.config.pageSize.toLong()
                val postItem = if (postId == null) null else localPostDataSource.getItemById(postId)

                if (postItem != null) {
                    if (loadType == LoadType.REFRESH) onDetailRefresh(pageSize, postItem)

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
                }

                return@withContext MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Log.e("UserPostRemoteMediator: fatal", "error: $e")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun onDetailRefresh(
        pageSize: Long,
        firstPost: OtherPostData?
    ) {
        val result = coroutineScope {
            val prev = async {
                Firebase.firestore.collection("post")
                    .whereEqualTo("authorId", authorId)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .endBefore(firstPost?.registerAt)
                    .limitToLast(pageSize)
                    .get()
                    .await()
                    .toObjects(PostDto::class.java)
                    .map { it.toPostModel() }
            }
            val next = async {
                Firebase.firestore.collection("post")
                    .whereEqualTo("authorId", authorId)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .startAfter(firstPost?.registerAt)
                    .limit(pageSize)
                    .get()
                    .await()
                    .toObjects(PostDto::class.java)
                    .map { it.toPostModel() }
            }
            prev.await() + next.await()
        }

        localPostDataSource.insertItems(
            result.map { it.toOtherData() },
            PostsUsageType.GALLERY
        )
    }

    private suspend fun onPrepend(pageSize: Long) {
        val firstItem = localPostDataSource.getFirstRandomItem()

        val result = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .endBefore(firstItem.registerAt)
            .limitToLast(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }

        localPostDataSource.insertItems(
            result.map { it.toOtherData() },
            PostsUsageType.GALLERY
        )
    }

    private suspend fun onAppend(pageSize: Long) {
        val lastItem = localPostDataSource.getLastRandomItem()

        val result = Firebase.firestore.collection("post")
            .whereEqualTo("authorId", authorId)
            .orderBy("registerAt", Query.Direction.DESCENDING)
            .startAfter(lastItem.registerAt)
            .limit(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
            .map { it.toPostModel() }

        localPostDataSource.insertItems(
            result.map { it.toOtherData() },
            PostsUsageType.GALLERY
        )
    }
}