package com.kolown.porring.core.data.repository


import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kolown.porring.core.data.api.datasource.local.LocalUserPrefDatasource
import com.kolown.porring.core.network.AuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

interface AuthRepository {
    suspend fun signInWithCredential(credential: Credential): Result<Unit>
    fun checkUserLoggedIn(): Flow<Boolean>
    fun logout(): Result<Unit>
    suspend fun joinWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun deleteAccount(password: String): Result<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    @Named("google") private val googleAuthDataSource: AuthDataSource,
    private val localUserPrefDatasourceImpl: LocalUserPrefDatasource,
) : AuthRepository {
    override suspend fun signInWithCredential(credential: Credential): Result<Unit> {
        return kotlin.runCatching {
            when (credential) {
                is CustomCredential -> handleCustomCredential(credential)
            }
        }
    }

    private suspend fun handleCustomCredential(credential: CustomCredential) {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            googleAuthDataSource.signInWithCredential(credential)
        }
    }

    override fun checkUserLoggedIn(): Flow<Boolean> {
        return googleAuthDataSource.checkUserLoggedIn()
    }

    override fun logout(): Result<Unit> {
        return googleAuthDataSource.logout()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return kotlin.runCatching {
            googleAuthDataSource.signInWithEmailAndPassword(email, password).getOrThrow()
                .let { userDto ->
                    localUserPrefDatasourceImpl.createUserData(userDto)
                }
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        val userId = googleAuthDataSource.getUserId()
        val email = localUserPrefDatasourceImpl.getUserEmail(userId).first()
        return googleAuthDataSource.deleteAccount(email, password)
    }

    override suspend fun joinWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return kotlin.runCatching {
            googleAuthDataSource.joinWithEmailAndPassword(email, password)
                .onSuccess { userDto ->
                    localUserPrefDatasourceImpl.createUserData(userDto)
                    googleAuthDataSource.logout()
                }
                .onFailure { e -> throw e }
        }
    }
}

