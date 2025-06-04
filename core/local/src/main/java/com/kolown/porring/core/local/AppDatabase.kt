package com.kolown.porring.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.dao.FollowerDao
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.entity.FollowerEntity
import com.kolown.porring.core.local.entity.HomePostKeyEntity
import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.local.entity.PagingPostKeyEntity
import com.kolown.porring.core.local.util.Converter

@Database(
    entities = [
        HomePostKeyEntity::class,
        PagingPostKeyEntity::class,
        OtherPostEntity::class,
        MyPostEntity::class,
        FollowerEntity::class,
    ],
    version = 17,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun followerDao(): FollowerDao
}