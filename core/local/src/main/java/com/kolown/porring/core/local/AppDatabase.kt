package com.kolown.porring.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kolown.porring.core.local.dao.FollowDao
import com.kolown.porring.core.local.dao.GalleryPostDao
import com.kolown.porring.core.local.dao.HomePostDao
import com.kolown.porring.core.local.dao.MyPostDao
import com.kolown.porring.core.local.dao.PostDao
import com.kolown.porring.core.local.dao.RandomPostDao
import com.kolown.porring.core.local.dao.ReactedPostDao
import com.kolown.porring.core.local.entity.FollowEntity
import com.kolown.porring.core.local.entity.GalleryPostKeyEntity
import com.kolown.porring.core.local.entity.HomePostKeyEntity
import com.kolown.porring.core.local.entity.MyPostEntity
import com.kolown.porring.core.local.entity.OtherPostEntity
import com.kolown.porring.core.local.entity.RandomPostKeyEntity
import com.kolown.porring.core.local.entity.ReactedPostEntity
import com.kolown.porring.core.local.util.Converter

@Database(
    entities = [
        HomePostKeyEntity::class,
        RandomPostKeyEntity::class,
        GalleryPostKeyEntity::class,
        OtherPostEntity::class,
        MyPostEntity::class,
        FollowEntity::class,
        ReactedPostEntity::class,
    ],
    version = 21,
    exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun homePostDao(): HomePostDao
    abstract fun randomPostDao(): RandomPostDao
    abstract fun galleryPostDao(): GalleryPostDao
    abstract fun myPostDao(): MyPostDao
    abstract fun followerDao(): FollowDao
    abstract fun reactedPostDao(): ReactedPostDao
}