package com.kolown.porring.core.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.local.entity.HomePostKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomePostDao {
    suspend fun insertHomePosts(
        posts: List<OtherPostData>
    ) {
        val homePostKeyEntities = posts.map { HomePostKeyEntity(postId = it.postId) }

        insertHomePostKeys(homePostKeyEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomePostKeys(homePostKeyEntity: List<HomePostKeyEntity>)

    @Transaction
    @Query(
        """
            SELECT
                home.post_id AS postId,
                
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
                    WHERE reacted_post.post_id = home.post_id
                    LIMIT 1
                ) AS myReaction,
                
                EXISTS (
                    SELECT 1
                    FROM follow
                    WHERE follow.id = other.author_id
                ) AS isFollowing
                
            FROM home_post_keys AS home
            LEFT JOIN other_post AS other ON home.post_id = other.post_id
        """
    )
    fun getHomePosts(): Flow<List<OtherPostData>>

    @Query("DELETE FROM home_post_keys")
    suspend fun clearHomeItems()
}