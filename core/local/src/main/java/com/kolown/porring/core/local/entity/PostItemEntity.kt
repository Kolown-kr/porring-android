package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_items")
data class HomeItemPostEntity(
    @PrimaryKey
    @ColumnInfo("post_id") val postId: String,
)

@Entity(tableName = "paging_items")
data class PagingItemPostEntity(
    @PrimaryKey
    @ColumnInfo("post_id") val postId: String,
    @ColumnInfo("sort_order") val sortOrder: Int,
)