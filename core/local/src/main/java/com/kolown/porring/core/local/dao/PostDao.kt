package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.LocalItemType
import com.kolown.porring.core.data.model.LocalPostDto
import com.kolown.porring.core.local.entity.DefaultPostInfoEntity
import com.kolown.porring.core.local.entity.HomeItemPostEntity
import com.kolown.porring.core.local.entity.OtherUserPostInfoEntity
import com.kolown.porring.core.local.entity.PagingItemPostEntity
import com.kolown.porring.core.local.mapper.toOtherUserPostInfo
import com.kolown.porring.core.local.mapper.toPostDefaultInfo
import com.kolown.porring.core.model.PostModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Transaction
    suspend fun insertItems(
        posts: List<PostModel>,
        localItemType: LocalItemType
    ) {
        insertPostDefaultInfo(posts.map { it.toPostDefaultInfo() })
        insertOtherUserPostInfo(posts.map { it.toOtherUserPostInfo() })
        when (localItemType) {
            LocalItemType.HOME_ITEM -> insertHomeItemPost(posts.map {
                HomeItemPostEntity(
                    postId = it.postId
                )
            })

            LocalItemType.PAGING_ITEM -> {
                var currentMax = getMaxSortOrder()

                val new = posts.map {
                    val new = ++currentMax

                    PagingItemPostEntity(postId = it.postId, sortOrder = new)
                }

                insertPagingItemPost(new)
            }
        }
    }

    @Transaction
    suspend fun clearAllItems() {
        clearHomeItems()
        clearPostDefaultInfo()
        clearOtherUserPostInfo()
        clearPagingItems()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostDefaultInfo(defaultPostInfoEntity: List<DefaultPostInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherUserPostInfo(otherUserPostInfoEntity: List<OtherUserPostInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeItemPost(homeItemPostEntity: List<HomeItemPostEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPagingItemPost(pagingItemPostEntity: List<PagingItemPostEntity>)

    @Query("SELECT COALESCE(MAX(sort_order), 0) FROM paging_items")
    suspend fun getMaxSortOrder(): Int

    @Transaction
    @Query(
        """
        SELECT
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
        FROM paging_items AS pagingItems
        LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
        LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
        ORDER BY
            CASE WHEN :useRegisterAt = 1 THEN post.register_at END DESC,
            pagingItems.sort_order ASC
    """
    )
    fun getPagingItems(useRegisterAt: Boolean = false): PagingSource<Int, LocalPostDto>

    @Transaction
    @Query(
        """
        SELECT 
            homeItems.post_id AS postId,
        
            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,
        
            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
        FROM home_items AS homeItems
        LEFT JOIN post_default_info AS post ON homeItems.post_id = post.post_id
        LEFT JOIN other_user_post_info AS other ON homeItems.post_id = other.post_id
    """
    )
    fun getItems(): Flow<List<LocalPostDto>>

    @Transaction
    @Query(
        """
            SELECT
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
            FROM paging_items AS pagingItems
            LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
            LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
            ORDER BY register_at DESC LIMIT 1
        """
    )
    fun getFirstPageItem(): LocalPostDto

    @Transaction
    @Query(
        """
            SELECT
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
            FROM paging_items AS pagingItems
            LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
            LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
            ORDER BY register_at ASC LIMIT 1
        """
    )
    fun getLastPageItem(): LocalPostDto

    @Transaction
    @Query(
        """
        SELECT 
            post.post_id AS postId,
            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,
            
            other.author_id AS authorId,
            other.my_reaction AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follower AS follower
                WHERE follower.follower_id = other.author_id
            ) AS isFollower
            
        FROM post_default_info AS post
        LEFT JOIN other_user_post_info AS other ON post.post_id = other.post_id
        WHERE post.post_id = :postId
    """
    )
    suspend fun getItemById(postId: String): LocalPostDto?

    @Query("UPDATE other_user_post_info SET my_reaction = :reaction WHERE post_id = :postId")
    suspend fun updateMyReaction(postId: String, reaction: Int?)

    @Query("UPDATE post_default_info SET reactions = :reactions WHERE post_id = :postId")
    suspend fun updateReactions(postId: String, reactions: List<Int>)

    @Query("DELETE FROM home_items")
    suspend fun clearHomeItems()

    @Query("DELETE FROM post_default_info")
    suspend fun clearPostDefaultInfo()

    @Query("DELETE FROM other_user_post_info")
    suspend fun clearOtherUserPostInfo()

    @Query("DELETE FROM paging_items")
    suspend fun clearPagingItems()

    @Transaction
    suspend fun deleteMyPost(postId: String) {
        deletePagingItem(postId)
    }

    @Query("DELETE FROM paging_items WHERE post_id = :postId")
    suspend fun deletePagingItem(postId: String)

    @Query("DELETE FROM post_default_info WHERE post_id = :postId")
    suspend fun deletePostDefaultInfo(postId: String)
}