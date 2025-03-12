package com.kolown.porring.core.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.local.room.dto.PostData
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.OtherUserPostInfo
import com.kolown.porring.core.local.room.entity.PagingItemPost
import com.kolown.porring.core.local.room.entity.PostDefaultInfo
import com.kolown.porring.core.local.toOtherUserPostInfo
import com.kolown.porring.core.local.toPostDefaultInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Transaction
    suspend fun insertItems(posts: List<PostData>, itemType: ItemType) {
        insertPostDefaultInfo(posts.map { it.toPostDefaultInfo() })
        insertOtherUserPostInfo(posts.map { it.toOtherUserPostInfo() })
        when (itemType) {
            ItemType.HOME_ITEM -> insertHomeItemPost(posts.map { HomeItemPost(postId = it.postId) })
            ItemType.PAGING_ITEM -> insertPagingItemPost(posts.map { PagingItemPost(postId = it.postId) })
        }
    }

    @Transaction
    suspend fun clearAllItems() {
        clearHomeItems()
        clearPostDefaultInfo()
        clearOtherUserPostInfo()
        clearPagingItems()
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPostDefaultInfo(postDefaultInfo: List<PostDefaultInfo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOtherUserPostInfo(otherUserPostInfo: List<OtherUserPostInfo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHomeItemPost(homeItemPost: List<HomeItemPost>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPagingItemPost(pagingItemPost: List<PagingItemPost>)

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
    fun getItems(): Flow<List<PostData>>

    @Transaction
    @Query(
        """
        SELECT
            pagingItems.paging_item_id AS pagingItemId,
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.is_follower AS isFollower,
            other.my_reaction AS myReaction
        FROM paging_items AS pagingItems
        LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
        LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
        ORDER BY
            CASE WHEN :useRegisterAt = 1 THEN post.register_at END DESC,
            pagingItems.paging_item_id ASC
    """
    )
    fun getPagingItems(useRegisterAt: Boolean = false): PagingSource<Int, PostData>

    @Transaction
    @Query(
        """
            SELECT
            pagingItems.paging_item_id AS pagingItemId,
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.is_follower AS isFollower,
            other.my_reaction AS myReaction
            FROM paging_items AS pagingItems
            LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
            LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
            ORDER BY register_at DESC LIMIT 1
        """
    )
    fun getFirstPageItem(): PostData

    @Transaction
    @Query(
        """
            SELECT
            pagingItems.paging_item_id AS pagingItemId,
            pagingItems.post_id AS postId,

            post.image_url AS imageUrl,
            post.register_at AS registerAt,
            post.description AS description,
            post.tags AS tags,
            post.reactions AS reactions,

            other.author_id AS authorId,
            other.is_follower AS isFollower,
            other.my_reaction AS myReaction
            FROM paging_items AS pagingItems
            LEFT JOIN post_default_info AS post ON pagingItems.post_id = post.post_id
            LEFT JOIN other_user_post_info AS other ON pagingItems.post_id = other.post_id
            ORDER BY register_at ASC LIMIT 1
        """
    )
    fun getLastPageItem(): PostData

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
    suspend fun getItemById(postId: String): PostData?

    @Query("UPDATE other_user_post_info SET my_reaction = :reaction WHERE post_id = :postId")
    suspend fun updateMyReaction(postId: String, reaction: Int?)

    @Query("UPDATE post_default_info SET reactions = :reactions WHERE post_id = :postId")
    suspend fun updateReactions(postId: String, reactions: List<Int>)

    @Query("UPDATE other_user_post_info SET is_follower = :isFollow WHERE author_id = :authorId")
    suspend fun updateFollowState(authorId: String, isFollow: Boolean)

    @Query("DELETE FROM home_items")
    suspend fun clearHomeItems()

    @Query("DELETE FROM post_default_info")
    suspend fun clearPostDefaultInfo()

    @Query("DELETE FROM other_user_post_info")
    suspend fun clearOtherUserPostInfo()

    @Query("DELETE FROM paging_items")
    suspend fun clearPagingItems()

}

enum class ItemType {
    HOME_ITEM,
    PAGING_ITEM,
}