package com.kolown.porring.core.data.di

import com.kolown.porring.core.data.datasource.fake.FakeGalleryDataSource
import com.kolown.porring.core.data.datasource.fake.GalleryDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

    @Fake
    @Singleton
    @Binds
    abstract fun bindFakeGalleryDataSource(
        galleryDataSource: FakeGalleryDataSource,
    ): GalleryDataSource

    @Binds
    abstract fun bindImageDataSource(
        imageDataSource: com.kolown.network.ImageDataSourceImpl,
    ): com.kolown.network.ImageDataSource

    @Binds
    abstract fun bindPostDataSource(
        postDataSource: com.kolown.network.PostDataSourceImpl,
    ): com.kolown.network.PostDataSource

    @Binds
    abstract fun bindTagDataSource(
        tagDataSource: com.kolown.network.TagDataSourceImpl,
    ): com.kolown.network.TagDataSource

    @Binds
    abstract fun bindReactionDataSource(
        reactionDataSource: com.kolown.network.ReactionDataSourceImpl,
    ): com.kolown.network.ReactionDataSource

    @Named("google")
    @Binds
    abstract fun bindsAuthDatsSource(
        authDataSource: com.kolown.network.AuthDataSourceImpl,
    ): com.kolown.network.AuthDataSource


    @Binds
    abstract fun bindUploadDataSource(
        followDataSource: com.kolown.network.FollowDataSourceImpl,
    ): com.kolown.network.FollowDataSource

}
