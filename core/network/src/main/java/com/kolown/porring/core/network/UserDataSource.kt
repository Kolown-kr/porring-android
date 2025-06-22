package com.kolown.porring.core.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kolown.porring.core.data.dto.ReactedPostDto
import com.kolown.porring.core.model.Follow
import com.kolown.porring.core.model.ReactedPost
import com.kolown.porring.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface UserDataSource {
    suspend fun createUserData(user: User): Result<Unit>

    suspend fun setReactedPost(userId: String, reactedPost: ReactedPost): Result<Unit>
    suspend fun deleteReactedPost(userId: String, postId: String): Result<Unit>
    suspend fun fetchAllReactedPost(userId: String): List<ReactedPostDto>

    suspend fun uploadFollow(userId: String, follow: Follow): Result<Unit>
    suspend fun fetchFollows(userId: String): List<Follow>
    suspend fun removeFollow(userId: String, followerId: String): Result<Unit>
}

class UserDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : UserDataSource {
    private val userCollection = firestore.collection("user")

    override suspend fun createUserData(user: User): Result<Unit> {
        return kotlin.runCatching {
            userCollection.document(user.userId).set(user).await()
        }
    }

    override suspend fun setReactedPost(userId: String, reactedPost: ReactedPost): Result<Unit> {
        return kotlin.runCatching {
            val data = mapOf(
                "reaction" to reactedPost.reaction,
                "registerAt" to reactedPost.registerAt
            )

            userCollection
                .document(userId)
                .collection("reacted_post")
                .document(reactedPost.postId)
                .set(data)
                .await()
        }
    }

    override suspend fun deleteReactedPost(userId: String, postId: String): Result<Unit> {
        return kotlin.runCatching {
            userCollection
                .document(userId)
                .collection("reacted_post")
                .document(postId)
                .delete()
        }
    }

    override suspend fun fetchAllReactedPost(userId: String): List<ReactedPostDto> {
        return userCollection
            .document(userId)
            .collection("reacted_post")
            .get()
            .await()
            .map { it.toObject(ReactedPostDto::class.java).copy(postId = it.id) }
    }

    override suspend fun uploadFollow(userId: String, follow: Follow): Result<Unit> {
        return runCatching {
            userCollection
                .document(userId)
                .collection("followers")
                .document(follow.id)
                .set(mapOf("followerName" to follow.name))
                .await()
        }
    }

    override suspend fun removeFollow(userId: String, followerId: String): Result<Unit> {
        return runCatching {
            val followerDoc = userCollection
                .document(userId)
                .collection("followers")
                .document(followerId)

            val docResult = followerDoc.get().await()

            if(!docResult.exists()) {
                throw IllegalStateException("팔로워 문서가 존재하지 않음")
            }

            followerDoc.delete().await()
        }
    }

    override suspend fun fetchFollows(userId: String): List<Follow> {
        val followers = userCollection
            .document(userId)
            .collection("followers")
            .get()
            .await()

        return if (followers.isEmpty) {
            emptyList()
        } else {
            followers.map {
                Follow(
                    id = it.id,
                    name = it.getString("followerName") ?: "NULL"
                )
            }
        }
    }

    override suspend fun getUserEmail(userId: String): String =
        userCollection.document(userId).get().await().getString(EMAIL).orEmpty()

    override suspend fun changeReceiverEmail(userId: String, email: String): Result<Unit> = kotlin.runCatching {
        userCollection.document(userId).update(RECEIVER_EMAIL, email).await()
    }

    companion object {
        private const val USER = "user"
        private const val EMAIL = "email"
        private const val RECEIVER_EMAIL = "receiverEmail"
    }
}
