package com.kolown.porring.core.data.di

import com.kolown.porring.core.data.datasource.fake.FakeGalleryDataSource
import com.kolown.porring.core.data.repository.GalleryRepository
import com.kolown.porring.core.data.repository.GalleryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModuleProvides {

    @Fake
    @Provides
    @Singleton
    fun provideGalleryRepositoryFake(): GalleryRepository {
        return GalleryRepositoryImpl(FakeGalleryDataSource())
    }
}
