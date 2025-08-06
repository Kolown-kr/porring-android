package com.kolown.porring.core.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.local.mapper.toEntity

@Dao
interface PostDao {
    // INSERT
    @Transaction
    suspend fun insertItems(
        posts: List<OtherPostData>,
        usageType: PostsUsageType
    ) {
        val otherPostsEntity = posts.map { it.toEntity() }

        insertOtherPostInfo(otherPostsEntity)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOtherPostInfo(otherPostEntity: List<OtherPostEntity>)

    // GET
    @Transaction
    suspend fun clearAllItems() {
        clearOtherUserPostInfo()
    }

    @Transaction
    @Query(
        """
        SELECT 
            other.post_id AS postId,
            other.image_url AS imageUrl,
            other.image_ratio AS imageRatio,
            other.register_at AS registerAt,
            other.description AS description,
            other.tags AS tags,
            other.reactions AS reactions,
            other.author_id AS authorId,
            
            (
                SELECT reaction
                FROM reacted_post
                WHERE reacted_post.post_id = other.post_id
                LIMIT 1
            ) AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follow 
                WHERE follow.id = other.author_id
            ) AS isFollowing
            
        FROM other_post AS other
        WHERE other.post_id = :postId
    """
    )
    suspend fun getItemById(postId: String): OtherPostData?

    @Query("UPDATE other_post SET my_reaction = :reaction WHERE post_id = :postId")
    suspend fun updateMyReaction(postId: String, reaction: Int?)

    @Query("UPDATE other_post SET reactions = :reactions WHERE post_id = :postId")
    suspend fun updateReactions(postId: String, reactions: List<Int>)

    @Query("DELETE FROM other_post")
    suspend fun clearOtherUserPostInfo()

}
