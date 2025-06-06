package com.kolown.porring.core.network

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.kolown.porring.core.model.Follow
import com.kolown.porring.core.network.model.FollowerDto
import com.kolown.porring.core.network.model.toFollowerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

interface FollowDataSource {
    suspend fun getIsFollower(userId: String, followerId: String): Result<Boolean>
    suspend fun uploadFollow(
        userId: String,
        follow: Follow
    ): Flow<Boolean>

    suspend fun fetchFollows(userId: String): List<Follow>
    suspend fun removeFollow(userId: String, followerId: String): Flow<Boolean>
    suspend fun getFollowerList(
        userId: String,
        key: String?,
        perPage: Long
    ): Result<List<Follow>>
}

class FollowDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : FollowDataSource {
    private val followCollection = firestore.collection("follow")
    private val userCollection = firestore.collection("user")

    override suspend fun getIsFollower(userId: String, followerId: String): Result<Boolean> {
        return runCatching {
            val result = followCollection.contains(userId, followerId).getOrElse {
                throw IOException("팔로우 확인 에러")
            }

            result.isEmpty.not()
        }.onFailure {
            Log.e("GetFollower", "getIsFollower: $it")
        }
    }

    override suspend fun getFollowerList(
        userId: String,
        key: String?,
        perPage: Long
    ): Result<List<Follow>> {
        return kotlin.runCatching {
            val followerIds = followCollection
                .whereEqualTo("userId", userId)
                .orderBy("followerId", Query.Direction.DESCENDING)
                .let { if (key != null) it.startAfter(key) else it }
                .limit(perPage)
                .get()
                .await()
                .map { it.toObject(FollowerDto::class.java).toFollowerModel() }

            followerIds
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
