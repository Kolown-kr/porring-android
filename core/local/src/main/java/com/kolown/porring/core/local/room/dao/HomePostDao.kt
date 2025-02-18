package com.kolown.porring.core.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kolown.porring.core.local.room.dto.PostDto
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface HomePostDao {
    @Transaction
    @Query("SELECT * FROM home_item_posts")
    fun getItems(): Flow<List<PostDto.HomeItemPostDto>>

    @Transaction
    @Query("SELECT * FROM home_item_posts WHERE post_id = :postId")
    suspend fun getItemById(postId: String): PostDto.HomeItemPostDto?

    @Transaction
    suspend fun insertItems(items: List<PostDto.HomeItemPostDto>) {
        val post = items.map { it.post }
        val homeItemPost = items.map { it.homeItemPost }

        insertPost(post)
        insertHomeItemPost(homeItemPost)
    }

    @Transaction
    suspend fun updateItem(item: PostDto.HomeItemPostDto) {
        updatePost(item.post)
        updateHomeItemPost(item.homeItemPost)
    }

    @Transaction
    suspend fun clearAllItems() {
        deletePostsWithHomeItemPostType()
        clearAllHomeItemPosts()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeItemPost(homeItemPosts: List<HomeItemPost>)

    @Update
    suspend fun updatePost(post: Post)

    @Update
    suspend fun updateHomeItemPost(homeItemPost: HomeItemPost)

    @Query("DELETE FROM posts WHERE type = 'HOME_ITEM_POST'")
    suspend fun deletePostsWithHomeItemPostType()

    @Query("DELETE FROM home_item_posts")
    suspend fun clearAllHomeItemPosts()

}