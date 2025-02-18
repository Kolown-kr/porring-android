package com.kolown.porring.core.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.room.dao.HomePostDao
import com.kolown.porring.core.local.room.entity.CurrentUserPost
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.OtherUserPost
import com.kolown.porring.core.local.room.entity.Post
import com.kolown.porring.core.local.room.util.Converter

@Database(
    entities = [
        Post::class,
        HomeItemPost::class,
        OtherUserPost::class,
        CurrentUserPost::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homePostDao(): HomePostDao
}