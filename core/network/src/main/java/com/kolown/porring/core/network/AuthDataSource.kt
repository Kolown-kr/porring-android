package com.kolown.porring.core.network

import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kolown.porring.core.model.User
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named

interface AuthDataSource {
    suspend fun signInWithCredential(credential: CustomCredential): Result<Unit>
    fun getUserId(): String
    fun getUserInfo(): User
    fun checkUserLoggedIn(): Flow<Boolean>
    fun logout(): Result<Unit>
    suspend fun joinWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun deleteAccount(email: String, password: String): Result<Unit>
}

@Named("google")
class AuthDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthDataSource {
    override suspend fun signInWithCredential(credential: CustomCredential): Result<Unit> {
        return kotlin.runCatching {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null).let {
                auth.signInWithCredential(it).await()
            }
        }
    }

    override fun getUserId(): String {
        val currentUser = auth.currentUser ?: return ""

        return "user-${currentUser.uid}"
    }

    override fun getUserInfo(): User {
        val currentUser = auth.currentUser ?: throw Exception("로그인 안된 유저")

        return User(
            userId = "user-${currentUser.uid}", email = currentUser.email.orEmpty(),
            createAt = currentUser.metadata?.creationTimestamp?.let {
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(it),
                    ZoneId.systemDefault()
                )
            } ?: ZonedDateTime.now()
        )
    }

    override fun checkUserLoggedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null).isSuccess
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }.buffer(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun logout(): Result<Unit> {
        return kotlin.runCatching {
            auth.signOut()
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<User> {
        return kotlin.runCatching {
            auth.signInWithEmailAndPassword(email, password).await().let { user ->
                User(
                    userId = "user-${user.user?.uid}",
                    email = user.user?.email.orEmpty(),
                    createAt = user.user?.metadata?.creationTimestamp?.let {
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(it),
                            ZoneId.systemDefault()
                        )
                    } ?: ZonedDateTime.now()
                )
            }
        }
    }

    override suspend fun deleteAccount(email: String, password: String): Result<Unit> =
        kotlin.runCatching {
            val user = auth.currentUser
            val credential = EmailAuthProvider.getCredential(email, password)
            user
                ?.reauthenticate(credential)
                ?.await()

            user
                ?.delete()
                ?.await()
        }

    override suspend fun joinWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<User> {
        return kotlin.runCatching {
            auth.createUserWithEmailAndPassword(email, password).await().let {
                User(
                    userId = "user-${it.user?.uid}",
                    email = it.user?.email.orEmpty()
                )
            }
        }
    }
}
