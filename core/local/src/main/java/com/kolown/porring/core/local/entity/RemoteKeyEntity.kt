package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val id: String = "posts",
    @ColumnInfo("prev_key") val prevKey: Long?,
    @ColumnInfo("next_key") val nextKey: Long?,
)
