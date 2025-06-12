package com.kolown.porring.core.network

import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun fetchAllReactedPost(userId: String): Result<List<ReactedPost>>

    suspend fun uploadFollow(userId: String, follow: Follow): Flow<Boolean>
    suspend fun fetchFollows(userId: String): List<Follow>
    suspend fun removeFollow(userId: String, followerId: String): Flow<Boolean>
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

    override suspend fun fetchAllReactedPost(userId: String): Result<List<ReactedPost>> {
        return kotlin.runCatching {
            userCollection
                .document(userId)
                .collection("reacted_post")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(ReactedPost::class.java) }
        }
    }

    override suspend fun uploadFollow(userId: String, follow: Follow): Flow<Boolean> = flow {
        userCollection
            .document(userId)
            .collection("followers")
            .document(follow.id)
            .set(mapOf("followerName" to follow.name))
            .await()
            .runCatching {
                emit(true)
            }.onFailure { _ ->
                emit(false)
            }
    }

    override suspend fun removeFollow(userId: String, followerId: String): Flow<Boolean> = flow {
        val followersCollection = userCollection
            .document(userId)
            .collection("followers")
        val followerDoc = followersCollection.document(followerId)
        val docResult = followerDoc.get().await()

        if (docResult.exists().not()) {
            emit(false)
        } else {
            followerDoc.delete().await()
            emit(true)
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
}
