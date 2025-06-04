package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_post")
data class MyPostEntity(
    @ColumnInfo("post_id")
    @PrimaryKey
    val postId: String,
    @ColumnInfo("image_url") val imageUrl: String,
    @ColumnInfo("register_at") val registerAt: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("tags") val tags: List<String>,
    @ColumnInfo("reactions") val reactions: List<Int>,
)
