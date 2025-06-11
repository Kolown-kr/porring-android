package com.kolown.porring.core.data.di

import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.AuthDataSourceImpl
import com.kolown.porring.core.network.ImageDataSource
import com.kolown.porring.core.network.ImageDataSourceImpl
import com.kolown.porring.core.network.PostDataSource
import com.kolown.porring.core.network.PostDataSourceImpl
import com.kolown.porring.core.network.TagDataSource
import com.kolown.porring.core.network.TagDataSourceImpl
import com.kolown.porring.core.network.UserDataSource
import com.kolown.porring.core.network.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

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

    @Named("google")
    @Binds
    abstract fun bindsAuthDatsSource(
        authDataSource: AuthDataSourceImpl,
    ): AuthDataSource

    @Binds
    abstract fun bindUploadDataSource(
        followDataSource: UserDataSourceImpl,
    ): UserDataSource
}
