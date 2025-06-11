package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reacted_post")
data class ReactedPostEntity(
    @PrimaryKey
    @ColumnInfo("post_id")
    val postId: String,
    val reaction: Int,
    @ColumnInfo("register_at")
    val registerAt: String,
)
