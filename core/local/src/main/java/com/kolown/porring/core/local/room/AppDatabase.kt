package com.kolown.porring.core.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.room.dao.PostDao
import com.kolown.porring.core.local.room.dao.RemoteKeyDao
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.OtherUserPostInfo
import com.kolown.porring.core.local.room.entity.PagingItemPost
import com.kolown.porring.core.local.room.entity.PostDefaultInfo
import com.kolown.porring.core.local.room.entity.RemoteKey
import com.kolown.porring.core.local.room.util.Converter

@Database(
    entities = [
        HomeItemPost::class,
        PagingItemPost::class,
        PostDefaultInfo::class,
        OtherUserPostInfo::class,
        RemoteKey::class,
    ],
    version = 13,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}