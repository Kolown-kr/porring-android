package com.kolown.porring.core.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "home_items",
)
data class HomeItemPost(
    @ColumnInfo("home_item_id")
    @PrimaryKey(autoGenerate = true)
    val homeItemId: Long = 0L,
    @ColumnInfo("post_id") val postId: String,
)