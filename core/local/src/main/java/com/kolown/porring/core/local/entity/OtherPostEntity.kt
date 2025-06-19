package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "other_post")
data class OtherPostEntity(
    @ColumnInfo("post_id")
    @PrimaryKey
    val postId: String,
    @ColumnInfo("author_id") val authorId: String,
    @ColumnInfo("image_url") val imageUrl: String,
    @ColumnInfo("image_ratio") val imageRatio: Float,
    @ColumnInfo("register_at") val registerAt: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("tags") val tags: List<String>,
    @ColumnInfo("reactions") val reactions: List<Int>,
    @ColumnInfo("my_reaction") val myReaction: Int?
)

