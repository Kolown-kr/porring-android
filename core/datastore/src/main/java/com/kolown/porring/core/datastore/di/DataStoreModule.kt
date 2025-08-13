package com.kolown.porring.core.datastore.di

import com.kolown.porring.core.data.api.datasource.local.LocalUserPrefDatasource
import com.kolown.porring.core.datastore.LocalUserPrefDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    abstract fun bindLocalUserPrefDatasource(localUserPrefDatasourceImpl: LocalUserPrefDatasourceImpl): LocalUserPrefDatasource
}