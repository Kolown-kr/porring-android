package com.kolown.porring.core.data.repository

import com.kolown.porring.core.datastore.LocalUserDataSource
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
}

class UserRepositoryImpl @Inject constructor(
    @Named("google") private val authDataSource: AuthDataSource,
    private val remoteUserDataSource: UserDataSource,
    private val localUserDataSource: LocalUserDataSource,
) : UserRepository {
    override suspend fun createUserData(): Result<Unit> {
        return kotlin.runCatching {
            authDataSource.getUserInfo().let {
                remoteUserDataSource.createUserData(it)
            }
        }
    }

    override suspend fun fetchUserReactedPost(): Result<Unit> {
        return kotlin.runCatching {
            val currentUserId = authDataSource.getUserId()

            remoteUserDataSource.fetchAllReactedPost(currentUserId)
        }
    }

    override fun checkUserId(authorId: String): Boolean {
        return authDataSource.getUserId() == authorId
    }

    override suspend fun getLatestUserEmail(): Flow<String> {
        return localUserDataSource.getUserEmail(authDataSource.getUserId())
    }

    override fun getUserData(): Result<String> {
        return runCatching {
            authDataSource.getUserId()
        }
    }
}
