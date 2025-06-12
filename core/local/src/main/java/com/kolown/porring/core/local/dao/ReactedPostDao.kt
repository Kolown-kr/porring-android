package com.kolown.porring.core.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kolown.porring.core.local.entity.ReactedPostEntity

@Dao
interface ReactedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReactedPosts(reactedPosts: List<ReactedPostEntity>)

    @Query(
        """
            SELECT reaction
            FROM reacted_post
            WHERE post_id = :postId
        """
    )
    suspend fun getReactionByPostId(postId: String): Int?

    @Query(
        """
            DELETE FROM reacted_post
            WHERE post_id = :postId
        """
    )
    suspend fun deleteReactedPost(postId: String)

    @Query("DELETE FROM reacted_post")
    suspend fun clearReactedPost()

}