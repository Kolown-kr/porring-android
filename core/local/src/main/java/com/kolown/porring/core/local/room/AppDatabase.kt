package com.kolown.porring.core.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.room.dao.PostDao
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.OtherUserPostInfo
import com.kolown.porring.core.local.room.entity.PostDefaultInfo
import com.kolown.porring.core.local.room.util.Converter

@Database(
    entities = [
        HomeItemPost::class,
        PostDefaultInfo::class,
        OtherUserPostInfo::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homePostDao(): PostDao
}