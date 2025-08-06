package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.local.entity.RandomPostKeyEntity

@Dao
interface RandomPostDao {
    @Transaction
    suspend fun insertRandomPosts(
        posts: List<OtherPostData>
    ) {
        var currentMax = getMaxSortOrder()
        val pagingPostKeyEntities = posts.map {
            RandomPostKeyEntity(postId = it.postId, sortOrder = ++currentMax)
        }

        insertRandomPostKeys(pagingPostKeyEntities)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRandomPostKeys(randomPostKeyEntity: List<RandomPostKeyEntity>)

    @Transaction
    @Query(
        """
            SELECT
                random.post_id AS postId,
                
                other.author_id AS authorId,
                other.image_url AS imageUrl,
                other.image_ratio AS imageRatio,
                other.register_at AS registerAt,
                other.description AS description,
                other.tags AS tags,
                other.reactions AS reactions,
                
                (
                    SELECT reaction
                    FROM reacted_post
                    WHERE reacted_post.post_id = random.post_id
                    LIMIT 1
                ) AS myReaction,
                
                EXISTS (
                    SELECT 1
                    FROM follow
                    WHERE follow.id = other.author_id
                ) AS isFollowing
                
            FROM random_post_keys AS random
            LEFT JOIN other_post AS other ON random.post_id = other.post_id
            ORDER BY random.sort_order ASC
        """
    )
    fun getRandomPosts(): PagingSource<Int, OtherPostData>

    @Query("DELETE FROM random_post_keys")
    suspend fun clearRandomItems()

    @Transaction
    @Query(
        """
            SELECT
            random.post_id AS postId,

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
                WHERE reacted_post.post_id = random.post_id
                LIMIT 1
            ) AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follow
                WHERE follow.id = other.author_id
            ) AS isFollowing
            
            FROM random_post_keys AS random
            LEFT JOIN other_post AS other ON random.post_id = other.post_id
            ORDER BY register_at DESC LIMIT 1
        """
    )
    fun getFirstRandomItem(): OtherPostData


    @Transaction
    @Query(
        """
            SELECT
            random.post_id AS postId,

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
                WHERE reacted_post.post_id = random.post_id
                LIMIT 1
            ) AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follow
                WHERE follow.id = other.author_id
            ) AS isFollowing
            
            FROM random_post_keys AS random
            LEFT JOIN other_post AS other ON random.post_id = other.post_id
            ORDER BY register_at ASC LIMIT 1
        """
    )
    fun getLastRandomItem(): OtherPostData

    @Query("SELECT COALESCE(MAX(sort_order), 0) FROM random_post_keys")
    suspend fun getMaxSortOrder(): Int
}