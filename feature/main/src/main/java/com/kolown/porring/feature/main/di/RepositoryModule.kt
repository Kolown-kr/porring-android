package com.kolown.porring.feature.main.di

import android.content.Context
import com.kolown.porring.core.data.repository.FakeImageRepository
import com.kolown.porring.core.data.repository.FakeImageRepositoryImpl
import com.kolown.porring.core.data.repository.ImageCacheRepository
import com.kolown.porring.core.data.repository.ImageCacheRepositoryImpl
import com.kolown.porring.core.data.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ImageCacheModule {
    @Provides
    @Singleton
    fun provideImageCacheRepository(@ApplicationContext applicationContext: Context): ImageCacheRepository {
        return ImageCacheRepositoryImpl(applicationContext)
    }
}


@Module
@InstallIn(SingletonComponent::class)
class ImageModule {
    @FakeImageRepository
    @Provides
    @Singleton
    fun provideFakeImageRepository(): ImageRepository {
        return FakeImageRepositoryImpl()
    }
}

