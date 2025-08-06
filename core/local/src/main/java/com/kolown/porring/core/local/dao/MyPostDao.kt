package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kolown.porring.core.data.model.MyPostData
import com.kolown.porring.core.local.entity.MyPostEntity

@Dao
interface MyPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyPost(posts: List<MyPostEntity>)

    @Query(
        """
            SELECT
                my.post_id AS postId,
                my.image_url AS imageUrl,
                my.image_ratio AS imageRatio,
                my.register_at AS registerAt,
                my.description AS description,
                my.tags AS tags,
                my.reactions AS reactions
            FROM my_post AS my
            ORDER BY register_at DESC
                
        """
    )
    fun getMyPosts(): PagingSource<Int, MyPostData>

    @Query("DELETE FROM my_post WHERE post_id = :postId")
    suspend fun deleteMyPost(postId: String)

    @Query("DELETE FROM my_post")
    suspend fun clearMyPost()
}