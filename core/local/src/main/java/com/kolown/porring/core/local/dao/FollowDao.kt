package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.local.entity.FollowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollows(follows: List<FollowEntity>)

    @Query("SELECT id, name FROM follow")
    fun getFollows(): PagingSource<Int, FollowData>

    @Query("SELECT name FROM follow WHERE id = :id")
    fun getFollowName(id: String): Flow<String?>

    @Query("DELETE FROM follow")
    suspend fun clearFollows()

    @Query("DELETE FROM follow WHERE id = :id")
    suspend fun deleteFollow(id: String)
}