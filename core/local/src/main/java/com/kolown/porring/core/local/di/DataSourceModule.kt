package com.kolown.porring.core.local.di

import com.kolown.porring.core.local.datasource.HomePostDataSourceImpl
import com.kolown.porring.core.local.datasource.LocalPostDataSource
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
}