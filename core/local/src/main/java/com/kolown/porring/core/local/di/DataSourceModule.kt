package com.kolown.porring.core.local.di

import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.local.datasource.DefaultLocalFollowDataSource
import com.kolown.porring.core.local.datasource.HomePostDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    @Named("local_post_datasource")
    abstract fun bindPostDataSource(postDataSource: HomePostDataSourceImpl): LocalPostDataSource

    @Binds
    @Named("local_follow_datasource")
    abstract fun bindFollowerDataSource(followerDataSource: DefaultLocalFollowDataSource): LocalFollowDataSource
}