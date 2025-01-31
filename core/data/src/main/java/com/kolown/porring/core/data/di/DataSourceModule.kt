package com.kolown.porring.core.data.di

import com.kolown.porring.core.data.datasource.fake.FakeGalleryDataSource
import com.kolown.porring.core.data.datasource.fake.GalleryDataSource
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.AuthDataSourceImpl
import com.kolown.porring.core.network.FollowDataSource
import com.kolown.porring.core.network.FollowDataSourceImpl
import com.kolown.porring.core.network.ImageDataSource
import com.kolown.porring.core.network.ImageDataSourceImpl
import com.kolown.porring.core.network.PostDataSource
import com.kolown.porring.core.network.PostDataSourceImpl
import com.kolown.porring.core.network.ReactionDataSource
import com.kolown.porring.core.network.ReactionDataSourceImpl
import com.kolown.porring.core.network.TagDataSource
import com.kolown.porring.core.network.TagDataSourceImpl
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
        imageDataSource: ImageDataSourceImpl,
    ): ImageDataSource

    @Binds
    abstract fun bindPostDataSource(
        postDataSource: PostDataSourceImpl,
    ): PostDataSource

    @Binds
    abstract fun bindTagDataSource(
        tagDataSource: TagDataSourceImpl,
    ): TagDataSource

    @Binds
    abstract fun bindReactionDataSource(
        reactionDataSource: ReactionDataSourceImpl,
    ): ReactionDataSource

    @Named("google")
    @Binds
    abstract fun bindsAuthDatsSource(
        authDataSource: AuthDataSourceImpl,
    ): AuthDataSource


    @Binds
    abstract fun bindUploadDataSource(
        followDataSource: FollowDataSourceImpl,
    ): FollowDataSource

}
