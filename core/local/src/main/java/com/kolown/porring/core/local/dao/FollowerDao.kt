package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kolown.porring.core.local.entity.FollowerEntity
import com.kolown.porring.core.model.Follower

@Dao
interface FollowerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowers(followers: List<FollowerEntity>)

    @Query(
        """
        SELECT 
            follower_id AS followerId,
            name AS followerName
        FROM follower 
    """
    )
    fun getFollowers(): PagingSource<Int, Follower>

    @Query("DELETE FROM follower")
    suspend fun clearFollowers()

    @Query("DELETE FROM follower WHERE follower_id = :followerId")
    suspend fun deleteFollower(followerId: String)
}