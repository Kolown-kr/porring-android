package com.kolown.porring.core.network

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kolown.porring.core.model.ReactionModel
import com.kolown.porring.core.network.model.ReactionDto
import com.kolown.porring.core.network.model.toReactionModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ReactionDataSource {
    suspend fun getReactionByPostId(postId: String): Result<List<ReactionModel>>
    suspend fun deletePostReaction(postId: String): Result<Unit>
}

class ReactionDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore,
) : ReactionDataSource {
    private val reactionCollection = firestore.collection("reaction")

    override suspend fun getReactionByPostId(postId: String): Result<List<ReactionModel>> {
        return kotlin.runCatching {
            reactionCollection
                .whereEqualTo("postId", postId)
                .get()
                .await()
                .let { snapshot ->
                    if (snapshot.isEmpty) {
                        emptyList()
                    } else {
                        snapshot.map { it.toObject(ReactionDto::class.java).toReactionModel() }
                    }
                }
        }
    }

    override suspend fun deletePostReaction(postId: String): Result<Unit> {
        return runCatching {
            val reactions = reactionCollection.whereEqualTo("postId", postId).get().await()

            coroutineScope {
                reactions.documents.map {
                    async {
                        reactionCollection.document(it.id).delete().await()
                    }
                }.awaitAll()
            }
        }
    }

    private suspend fun CollectionReference.contains(
        postId: String,
        userId: String,
    ): Result<QuerySnapshot> {
        return kotlin.runCatching {
            this
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
        }
    }
}
