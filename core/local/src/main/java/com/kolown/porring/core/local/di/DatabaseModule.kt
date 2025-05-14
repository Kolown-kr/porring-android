package com.kolown.porring.core.local.di

import android.content.Context
import androidx.room.Room
import com.kolown.porring.core.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "porring_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePostDao(database: AppDatabase) = database.postDao()
}