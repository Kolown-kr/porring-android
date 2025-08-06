package com.kolown.porring.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kolown.porring.core.data.model.OtherPostData
import com.kolown.porring.core.local.entity.GalleryPostKeyEntity

@Dao
interface GalleryPostDao {
    suspend fun insertGalleryPosts(
        posts: List<OtherPostData>
    ) {
        posts
            .map { GalleryPostKeyEntity(postId = it.postId) }
            .let { insertGalleryPostKeys(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPostKeys(galleryPostKeyEntity: List<GalleryPostKeyEntity>)

    @Transaction
    @Query(
        """
            SELECT
                gallery.post_id AS postId,
                
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
                    WHERE reacted_post.post_id = gallery.post_id
                    LIMIT 1
                ) AS myReaction,
                
                EXISTS (
                    SELECT 1
                    FROM follow
                    WHERE follow.id = other.author_id
                ) AS isFollowing
                
            FROM gallery_post_keys AS gallery
            LEFT JOIN other_post AS other ON gallery.post_id = other.post_id
            ORDER BY other.register_at DESC
        """
    )
    fun getGalleryPosts(): PagingSource<Int, OtherPostData>

    @Query("DELETE FROM gallery_post_keys")
    suspend fun clearGalleryItems()

    @Transaction
    @Query(
        """
            SELECT
            gallery.post_id AS postId,

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
                WHERE reacted_post.post_id = gallery.post_id
                LIMIT 1
            ) AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follow
                WHERE follow.id = other.author_id
            ) AS isFollowing
            
            FROM gallery_post_keys AS gallery
            LEFT JOIN other_post AS other ON gallery.post_id = other.post_id
            ORDER BY register_at DESC LIMIT 1
        """
    )
    fun getFirstGalleryItem(): OtherPostData


    @Transaction
    @Query(
        """
            SELECT
            gallery.post_id AS postId,

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
                WHERE reacted_post.post_id = gallery.post_id
                LIMIT 1
            ) AS myReaction,
            
            EXISTS (
                SELECT 1
                FROM follow
                WHERE follow.id = other.author_id
            ) AS isFollowing
            
            FROM gallery_post_keys AS gallery
            LEFT JOIN other_post AS other ON gallery.post_id = other.post_id
            ORDER BY register_at ASC LIMIT 1
        """
    )
    fun getLastGalleryItem(): OtherPostData
}