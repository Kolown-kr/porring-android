package com.kolown.porring.core.data.repository

import com.kolown.porring.core.data.api.datasource.local.LocalUserCacheDataSource
import com.kolown.porring.core.data.api.datasource.local.LocalUserPrefDatasource
import com.kolown.porring.core.data.mapper.toData
import com.kolown.porring.core.data.mapper.toModel
import com.kolown.porring.core.model.User
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.UserDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

interface UserRepository {
    suspend fun createUserData(): Result<Unit>
    fun checkUserId(authorId: String): Boolean
    suspend fun getLatestUserEmail(): Flow<String>
    fun getUserData(): Result<String>

    suspend fun fetchUserReactedPost(): Result<Unit>
    suspend fun clearReactedPostCache()

    suspend fun getCurrentUser(): Result<User>
    suspend fun changeReceiverEmailEmail(email: String): Result<Unit>
    suspend fun deleteAccount(
        email: String,
        password: String
    ): Result<Unit>
}

class UserRepositoryImpl @Inject constructor(
    @Named("google") private val authDataSource: AuthDataSource,
    private val remoteUserDataSource: UserDataSource,
    private val localUserCacheDataSource: LocalUserCacheDataSource,
    private val localUserPrefDatasourceImpl: LocalUserPrefDatasource,
) : UserRepository {
    override suspend fun createUserData(): Result<Unit> {
        return kotlin.runCatching {
            authDataSource.getUserInfo().let { user ->
                user.onSuccess {
                    remoteUserDataSource.createUserData(it)
                }
            }
        }
    }

    override fun checkUserId(authorId: String): Boolean {
        return authDataSource.getUserId() == authorId
    }

    override suspend fun getLatestUserEmail(): Flow<String> {
        return localUserPrefDatasourceImpl.getUserEmail(authDataSource.getUserId())
    }

    override suspend fun getCurrentUser() =
        authDataSource.getUserInfo()

    override suspend fun changeReceiverEmailEmail(email: String): Result<Unit> =
        remoteUserDataSource.changeReceiverEmail(authDataSource.getUserId(), email)

    override suspend fun deleteAccount(email: String, password: String): Result<Unit> =
        authDataSource.deleteAccount(email, password)

    override fun getUserData(): Result<String> {
        return runCatching {
            authDataSource.getUserId()
        }
    }

    override suspend fun fetchUserReactedPost(): Result<Unit> = kotlin.runCatching {
        val currentUserId = authDataSource.getUserId()
        val reactedPosts =
            remoteUserDataSource.fetchAllReactedPost(currentUserId).map { it.toModel() }

        localUserCacheDataSource.insertReactedPosts(reactedPosts.map { it.toData() })
    }

    override suspend fun clearReactedPostCache() {
        localUserCacheDataSource.clearReactedPost()
    }
}
