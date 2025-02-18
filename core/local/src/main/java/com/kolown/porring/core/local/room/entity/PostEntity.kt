package com.kolown.porring.core.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    @ColumnInfo("post_id")
    val id: String,
    @ColumnInfo("image_url") val imageUrl: String,
    @ColumnInfo("register_at") val registerAt: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("tags") val tags: List<String>,
    @ColumnInfo("reactions") val reactions: List<Int>,
    @ColumnInfo("type") val type: PostType,
)

@Entity(
    tableName = "home_item_posts",
    primaryKeys = ["post_id"],
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["post_id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HomeItemPost(
    @ColumnInfo("post_id")
    val postId: String,
    @ColumnInfo("author_id") val authorId: String,
    @ColumnInfo("is_follower") val isFollower: Boolean,
    @ColumnInfo("my_reaction") val myReaction: Int?,
)

@Entity(
    tableName = "other_user_posts",
    primaryKeys = ["post_id"],
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["post_id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OtherUserPost(
    @ColumnInfo("post_id")
    val postId: String,
    @ColumnInfo("author_id") val authorId: String,
    @ColumnInfo("is_follower") val isFollower: Boolean,
    @ColumnInfo("my_reaction") val myReaction: Int?,
)

@Entity(
    tableName = "current_user_posts",
    primaryKeys = ["post_id"],
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["post_id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CurrentUserPost(
    @ColumnInfo("post_id")
    val postId: String,
)

enum class PostType {
    HOME_ITEM_POST,
    OTHER_USER,
    CURRENT_USER
}