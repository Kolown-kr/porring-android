package com.kolown.porring.core.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "post_default_info",
    primaryKeys = ["post_id"]
)
data class PostDefaultInfo(
    @ColumnInfo("post_id") val postId: String,
    @ColumnInfo("image_url") val imageUrl: String,
    @ColumnInfo("register_at") val registerAt: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("tags") val tags: List<String>,
    @ColumnInfo("reactions") val reactions: List<Int>,
)

