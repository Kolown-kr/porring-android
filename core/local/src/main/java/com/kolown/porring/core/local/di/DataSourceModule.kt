package com.kolown.porring.core.local.di

import com.kolown.porring.core.data.api.datasource.local.LocalFollowDataSource
import com.kolown.porring.core.data.api.datasource.local.LocalPostDataSource
import com.kolown.porring.core.local.datasource.DefaultLocalFollowDataSource
import com.kolown.porring.core.local.datasource.DefaultLocalPostDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindPostDataSource(postDataSource: DefaultLocalPostDataSource): LocalPostDataSource

    @Binds
    abstract fun bindFollowDataSource(followDataSource: DefaultLocalFollowDataSource): LocalFollowDataSource
}