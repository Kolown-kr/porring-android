package com.kolown.porring.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.dao.RemoteKeyDao
import com.kolown.porring.core.local.entity.DefaultPostInfoEntity
import com.kolown.porring.core.local.entity.HomeItemPostEntity
import com.kolown.porring.core.local.entity.OtherUserPostInfoEntity
import com.kolown.porring.core.local.entity.PagingItemPostEntity
import com.kolown.porring.core.local.entity.RemoteKeyEntity
import com.kolown.porring.core.local.util.Converter

@Database(
    entities = [
        HomeItemPostEntity::class,
        PagingItemPostEntity::class,
        DefaultPostInfoEntity::class,
        OtherUserPostInfoEntity::class,
        RemoteKeyEntity::class,
    ],
    version = 14,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}