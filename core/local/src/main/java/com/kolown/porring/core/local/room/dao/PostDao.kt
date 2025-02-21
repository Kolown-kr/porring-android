package com.kolown.porring.core.local.room.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.local.room.dto.PostDto
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.OtherUserPostInfo
import com.kolown.porring.core.local.room.entity.PostDefaultInfo
import com.kolown.porring.core.local.toOtherUserPostInfo
import com.kolown.porring.core.local.toPostDefaultInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Transaction
    suspend fun insertItems(posts: List<PostDto>) {
        Log.e("refreshErrorTest", posts.toString())
        insertPostDefaultInfo(posts.map { it.toPostDefaultInfo() })
        insertOtherUserPostInfo(posts.map { it.toOtherUserPostInfo() })
        insertHomeItemPost(posts.map { HomeItemPost(postId = it.postId) })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostDefaultInfo(postDefaultInfo: List<PostDefaultInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherUserPostInfo(otherUserPostInfo: List<OtherUserPostInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeItemPost(homeItemPost: List<HomeItemPost>)

    @Transaction
    @Query(
        """
        SELECT 
            homeItems.home_item_id AS homeItemId,
            homeItems.post_id AS postId,
        
            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,
        
            other.author_id AS authorId,
            other.is_follower AS isFollower,
            other.my_reaction AS myReaction
        FROM home_items AS homeItems
        LEFT JOIN post_default_info AS post ON homeItems.post_id = post.post_id
        LEFT JOIN other_user_post_info AS other ON homeItems.post_id = other.post_id
    """
    )
    fun getItems(): Flow<List<PostDto>>

    @Transaction
    @Query(
        """
        SELECT 
            post.post_id AS postId,
            other.author_id AS authorId,
            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            other.is_follower AS isFollower,
            post.reactions AS reactions,
            other.my_reaction AS myReaction
        FROM post_default_info AS post
        LEFT JOIN other_user_post_info AS other ON post.post_id = other.post_id
        WHERE post.post_id = :postId
    """
    )
    suspend fun getItemById(postId: String): PostDto?

    @Query("UPDATE other_user_post_info SET my_reaction = :reaction WHERE post_id = :postId")
    suspend fun updateMyReaction(postId: String, reaction: Int?)

    @Query("UPDATE post_default_info SET reactions = :reactions WHERE post_id = :postId")
    suspend fun updateReactions(postId: String, reactions: List<Int>)

    @Query("DELETE FROM home_items")
    suspend fun clearHomeItems()
}
