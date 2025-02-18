package com.kolown.porring.core.local.di

import com.kolown.porring.core.local.HomePostDataSourceImpl
import com.kolown.porring.core.local.LocalPostDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
abstract class PostDataSourceModule {
    @Binds
    @Named("home_post_datasource")
    abstract fun bindHomePostDataSource(postDataSource: HomePostDataSourceImpl): LocalPostDataSource
}