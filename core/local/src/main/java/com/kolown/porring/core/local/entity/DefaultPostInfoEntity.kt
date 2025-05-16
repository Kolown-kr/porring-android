package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_default_info")
data class DefaultPostInfoEntity(
    @ColumnInfo("post_id")
    @PrimaryKey
    val postId: String,
    @ColumnInfo("image_url") val imageUrl: String,
    @ColumnInfo("register_at") val registerAt: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("tags") val tags: List<String>,
    @ColumnInfo("reactions") val reactions: List<Int>,
)

@Entity(tableName = "other_user_post_info")
data class OtherUserPostInfoEntity(
    @ColumnInfo("post_id")
    @PrimaryKey
    val postId: String,
    @ColumnInfo("author_id") val authorId: String,
    @ColumnInfo("my_reaction") val myReaction: Int?
)

