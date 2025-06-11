package com.kolown.porring.core.network

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kolown.porring.core.model.Follow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FollowDataSource {
    suspend fun uploadFollow(
        userId: String,
        follow: Follow
    ): Flow<Boolean>

    suspend fun fetchFollows(userId: String): List<Follow>
    suspend fun removeFollow(userId: String, followerId: String): Flow<Boolean>
}

class FollowDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : FollowDataSource {
    private val userCollection = firestore.collection("user")

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

    private suspend fun CollectionReference.contains(
        userId: String,
        followerId: String,
    ): Result<QuerySnapshot> {
        return runCatching {
            this
                .whereEqualTo("userId", userId)
                .whereEqualTo("followerId", followerId)
                .get()
                .await()
        }.onFailure {
            Log.e("FollowContains", "contains: $it")
        }
    }
}
