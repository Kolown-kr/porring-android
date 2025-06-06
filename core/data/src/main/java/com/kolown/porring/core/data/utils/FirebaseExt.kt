package com.kolown.porring.core.data.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import kotlinx.coroutines.tasks.await


internal suspend fun FirebaseFirestore.fetchRandomPost(
    userId: String,
    randomType: String,
    seed: Long,
    pageSize: Long
): List<PostModel> {
    suspend fun createQuery(seed: Long, pageSize: Long): List<PostDto> {
        return this.collection("post")
            .whereNotEqualTo("authorId", userId)
            .whereGreaterThan("random$randomType", seed)
            .orderBy("random$randomType", Query.Direction.ASCENDING)
            .orderBy("postId")
            .limit(pageSize)
            .get()
            .await()
            .toObjects(PostDto::class.java)
    }

    val initialPosts = createQuery(seed, pageSize)
    val resultPosts = if (initialPosts.size < pageSize) {
        val additionalPosts = createQuery(0, pageSize - initialPosts.size)

        initialPosts + additionalPosts
    } else {
        initialPosts
    }

    return resultPosts.map { it.toPostModel(randomType) }
}