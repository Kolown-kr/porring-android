package com.kolown.porring.core.data.api.datasource.local

import androidx.paging.PagingSource
import com.kolown.porring.core.data.model.FollowData
import kotlinx.coroutines.flow.Flow

interface LocalUserDataSource {
    suspend fun insertFollows(follows: List<FollowData>)
    fun getFollowName(id: String): Flow<String?>
    fun getFollows(): PagingSource<Int, FollowData>
    suspend fun clearFollows()
    suspend fun deleteFollow(id: String)
}