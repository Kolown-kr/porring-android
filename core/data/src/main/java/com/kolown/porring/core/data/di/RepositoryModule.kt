package com.kolown.porring.core.data.di

import android.content.Context
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.AuthRepositoryImpl
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.FollowRepositoryImpl
import com.kolown.porring.core.data.repository.ImageCacheRepository
import com.kolown.porring.core.data.repository.ImageCacheRepositoryImpl
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.PostRepositoryImpl
import com.kolown.porring.core.data.repository.RemoteConfigRepository
import com.kolown.porring.core.data.repository.RemoteConfigRepositoryImpl
import com.kolown.porring.core.data.repository.TagRepository
import com.kolown.porring.core.data.repository.TagRepositoryImpl
import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.data.repository.UserRepositoryImpl
import com.kolown.porring.core.network.RemoteConfigDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ProvideRepositoryModule{
    @Provides
    @Singleton
    fun providesRemoteConfigRepository(): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(RemoteConfigDataSource())
    }

    @Provides
    @Singleton
    fun provideImageCacheRepository(@ApplicationContext applicationContext: Context): ImageCacheRepository {
        return ImageCacheRepositoryImpl(applicationContext)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providePostRepository(
        postRepository: PostRepositoryImpl,
    ): PostRepository

    @Binds
    abstract fun provideAuthRepository(
        authRepository: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    abstract fun provideUserRepository(
        userRepository: UserRepositoryImpl,
    ): UserRepository

    @Binds
    abstract fun provideFollowRepository(
        followRepository: FollowRepositoryImpl,
    ): FollowRepository

    @Binds
    abstract fun provideTagRepository(
        tagRepository: TagRepositoryImpl,
    ): TagRepository

}
