package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.MyPostDto
import com.kolown.porring.core.data.model.OtherPostDto
import com.kolown.porring.core.data.model.PostsUsageType
import com.kolown.porring.core.local.entity.HomePostKeyEntity
import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.local.entity.PagingPostKeyEntity
import com.kolown.porring.core.local.mapper.toEntity
import com.kolown.porring.core.model.PostModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // OtherPost 연관 쿼리 =========================================================================
    // INSERT
    @Transaction
    suspend fun insertItems(
        posts: List<PostModel>,
        postsUsageType: PostsUsageType
    ) {
        val otherPostsEntity = posts.map { it.toEntity() }

        insertOtherPostInfo(otherPostsEntity)
        insertUsageKey(posts, postsUsageType)

    }

    private suspend fun insertUsageKey(
        posts: List<PostModel>,
        usageType: PostsUsageType
    ) {
        when (usageType) {
            PostsUsageType.HOME -> {
                val homePostKeyEntities = posts.map { HomePostKeyEntity(postId = it.postId) }

                insertHomePostKeys(homePostKeyEntities)
            }

            PostsUsageType.PAGING -> {
                var currentMax = getMaxSortOrder()

                val pagingPostKeyEntities = posts.map {
                    val newOrder = ++currentMax

                    PagingPostKeyEntity(postId = it.postId, sortOrder = newOrder)
                }

                insertPagingPostKeys(pagingPostKeyEntities)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherPostInfo(otherPostEntity: List<OtherPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomePostKeys(homePostKeyEntity: List<HomePostKeyEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPagingPostKeys(pagingPostKeyEntity: List<PagingPostKeyEntity>)

    @Query("SELECT COALESCE(MAX(sort_order), 0) FROM paging_post_keys")
    suspend fun getMaxSortOrder(): Int

    // GET
    @Transaction
    @Query(
        """
            SELECT
                home.post_id AS postId,
                
                other.author_id AS authorId,
                other.image_url AS imageUrl,
                other.register_at AS registerAt,
                other.description AS description,
                other.tags AS tags,
                other.reactions AS reactions,
                other.my_reaction AS myReaction,
                
                EXISTS (
                    SELECT 1
                    FROM follower
                    WHERE follower.follower_id = other.author_id
                ) AS isFollower
                
            FROM home_post_keys AS home
            LEFT JOIN other_post AS other ON home.post_id = other.post_id
        """
    )
    fun getHomePosts(): Flow<List<OtherPostDto>>

    @Transaction
    @Query(
        """
            SELECT
                paging.post_id AS postId,
                
                other.author_id AS authorId,
                other.image_url AS imageUrl,
                other.register_at AS registerAt,
                other.description AS description,
                other.tags AS tags,
                other.reactions AS reactions,
                other.my_reaction AS myReaction,
                
                EXISTS (
                    SELECT 1
                    FROM follower
                    WHERE follower.follower_id = other.author_id
                ) AS isFollower
                
            FROM paging_post_keys AS paging
            LEFT JOIN other_post AS other ON paging.post_id = other.post_id
            ORDER BY paging.sort_order ASC
        """
    )
    fun getPagingPosts(): PagingSource<Int, OtherPostDto>

    @Transaction
    suspend fun clearAllItems() {
        clearHomeItems()
        clearOtherUserPostInfo()
        clearPagingItems()
    }

    @Transaction
    @Query(
        """
            SELECT
            paging.post_id AS postId,

            other.image_url AS imageUrl,
            other.register_at AS registerAt,
            other.description AS description,
            other.tags AS tags,
            other.reactions AS reactions,
            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
            FROM paging_post_keys AS paging
            LEFT JOIN other_post AS other ON paging.post_id = other.post_id
            ORDER BY register_at DESC LIMIT 1
        """
    )
    fun getFirstPageItem(): OtherPostDto


    @Transaction
    @Query(
        """
            SELECT
            paging.post_id AS postId,

            other.image_url AS imageUrl,
            other.register_at AS registerAt,
            other.description AS description,
            other.tags AS tags,
            other.reactions AS reactions,
            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
            FROM paging_post_keys AS paging
            LEFT JOIN other_post AS other ON paging.post_id = other.post_id
            ORDER BY register_at ASC LIMIT 1
        """
    )
    fun getLastPageItem(): OtherPostDto

    @Transaction
    @Query(
        """
        SELECT 
            other.post_id AS postId,
            other.image_url AS imageUrl,
            other.register_at AS registerAt,
            other.description AS description,
            other.tags AS tags,
            other.reactions AS reactions,
            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
        FROM other_post AS other
        WHERE other.post_id = :postId
    """
    )
    suspend fun getItemById(postId: String): OtherPostDto?

    @Query("UPDATE other_post SET my_reaction = :reaction WHERE post_id = :postId")
    suspend fun updateMyReaction(postId: String, reaction: Int?)

    @Query("UPDATE other_post SET reactions = :reactions WHERE post_id = :postId")
    suspend fun updateReactions(postId: String, reactions: List<Int>)

    @Query("DELETE FROM home_post_keys")
    suspend fun clearHomeItems()

    @Query("DELETE FROM other_post")
    suspend fun clearOtherUserPostInfo()

    @Query("DELETE FROM paging_post_keys")
    suspend fun clearPagingItems()

    // MyPost 연관 쿼리 ============================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyPost(posts: List<MyPostEntity>)

    @Query(
        """
            SELECT
                my.post_id AS postId,
                my.image_url AS imageUrl,
                my.register_at AS registerAt,
                my.description AS description,
                my.tags AS tags,
                my.reactions AS reactions
            FROM my_post AS my
            ORDER BY register_at DESC
                
        """
    )
    fun getMyPosts(): PagingSource<Int, MyPostDto>

    @Query("DELETE FROM my_post WHERE post_id = :postId")
    suspend fun deleteMyPost(postId: String)
}
