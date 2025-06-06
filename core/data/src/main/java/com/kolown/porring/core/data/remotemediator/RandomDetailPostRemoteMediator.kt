package com.kolown.porring.core.data.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.data.mapper.toOtherData
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.utils.fetchRandomPost
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.network.AuthDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Named

@OptIn(ExperimentalPagingApi::class)
class RandomDetailPostRemoteMediator @AssistedInject constructor(
    @Assisted private val pageState: StateFlow<PageState>,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    private val localPostDataSource: LocalPostDataSource,
) : RemoteMediator<Int, OtherPostData>() {
    private val firestore = Firebase.firestore
    private var isLoading = false
    private var nextKey = 0L
    private val currentUserId = googleAuthDataSource.getUserId()
    private var randomType = "ABCDE".random().toString()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OtherPostData>
    ): MediatorResult {
        return try {
            withContext(Dispatchers.IO) {
                val pageSize = state.config.pageSize.toLong()

                if (loadType == LoadType.REFRESH) {
                    nextKey = (0..Long.MAX_VALUE).random()
                    fetchPosts(nextKey, pageSize)
                }

                if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(
                    endOfPaginationReached = true
                )

                pageState.collectLatest { pageState ->
                    if (isLoading.not() && pageState.pageCount - state.config.pageSize - 1 < pageState.currentPage) {
                        isLoading = true
                        launch {
                            try {
                                fetchPosts(nextKey, pageSize)
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

    private suspend fun fetchPosts(key: Long, pageSize: Long) {
        val posts = firestore.fetchRandomPost(
            userId = currentUserId,
            randomType = randomType,
            seed = key,
            pageSize = pageSize
        )

        localPostDataSource.insertItems(
            posts.map { it.toOtherData() },
            com.kolown.porring.core.data.model.PostsUsageType.PAGING
        )

        val currentLastKey = posts.lastOrNull()?.random ?: nextKey

        nextKey = currentLastKey + 1
    }
}