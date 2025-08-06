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
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class UserGalleryPostRemoteMediator @AssistedInject constructor(
    @Assisted("author_id") private val authorId: String,
    private val localPostDataSource: LocalPostDataSource,
) : RemoteMediator<Int, OtherPostData>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OtherPostData>
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
            Log.e("GalleryPostRemoteMediator: fatal", "error: $e")
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

        localPostDataSource.insertItems(
            posts.map { it.toOtherData() },
            com.kolown.porring.core.data.model.PostsUsageType.GALLERY
        )

        return MediatorResult.Success(endOfPaginationReached = posts.isEmpty())
    }

    private suspend fun onAppend(pageSize: Long): MediatorResult {
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
            com.kolown.porring.core.data.model.PostsUsageType.GALLERY
        )

        return MediatorResult.Success(endOfPaginationReached = result.isEmpty())
    }
}