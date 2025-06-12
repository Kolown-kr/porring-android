package com.kolown.porring.core.data.repository

import com.kolown.porring.core.data.api.datasource.local.LocalUserCacheDataSource
import com.kolown.porring.core.data.api.datasource.local.LocalUserPrefDatasource
import com.kolown.porring.core.data.mapper.toData
import com.kolown.porring.core.data.mapper.toModel
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

interface UserRepository {
    suspend fun createUserData(): Result<Unit>
    fun checkUserId(authorId: String): Boolean
    suspend fun getLatestUserEmail(): Flow<String>
    fun getUserData(): Result<String>

    suspend fun fetchUserReactedPost(): Result<Unit>
    suspend fun clearReactedPostCache()
}

class UserRepositoryImpl @Inject constructor(
    @Named("google") private val authDataSource: AuthDataSource,
    private val remoteUserDataSource: UserDataSource,
    private val localUserCacheDataSource: LocalUserCacheDataSource,
    private val localUserPrefDatasourceImpl: LocalUserPrefDatasource,
) : UserRepository {
    override suspend fun createUserData(): Result<Unit> {
        return kotlin.runCatching {
            authDataSource.getUserInfo().let {
                remoteUserDataSource.createUserData(it)
            }
        }
    }

    override fun checkUserId(authorId: String): Boolean {
        return authDataSource.getUserId() == authorId
    }

    override suspend fun getLatestUserEmail(): Flow<String> {
        return localUserPrefDatasourceImpl.getUserEmail(authDataSource.getUserId())
    }

    override fun getUserData(): Result<String> {
        return runCatching {
            authDataSource.getUserId()
        }
    }

    override suspend fun fetchUserReactedPost(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            val currentUserId = authDataSource.getUserId()
            val reactedPosts =
                remoteUserDataSource.fetchAllReactedPost(currentUserId).map { it.toModel() }

            localUserCacheDataSource.insertReactedPosts(reactedPosts.map { it.toData() })
        }
    }

    override suspend fun clearReactedPostCache() {
        localUserCacheDataSource.clearReactedPost()
    }
}
