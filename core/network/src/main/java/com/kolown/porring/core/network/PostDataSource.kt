package com.kolown.porring.core.network

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PostDataSource {
    suspend fun uploadPost(
        authorId: String,
        description: String,
        imageUrl: String,
        imageRatio: Float,
        tags: List<String>
    ): Result<String>

    suspend fun updateImageUrl(documentId: String, imageUrl: String)

    suspend fun fetchRandomPost(
        uid: String,
        count: Int,
        randomType: String
    ): Result<List<PostModel>>

    suspend fun getPostBySearch(
        currentUserId: String,
        postIds: List<String>,
        key: String?,
        perPage: Long
    ): Result<List<PostModel>>

    suspend fun fetchPostWithAuthorId(authorId: String, limit: Long): Result<List<PostModel>>

    fun setPostReaction(postId: String, reaction: Int, prevReaction: Int?)
    fun deletePostReaction(postId: String, reaction: Int)

    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun getAllPostsByAuthorId(authorId: String): Result<List<PostModel>>
}

class PostDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : PostDataSource {
    private val postCollection = firestore.collection("post")
    private var randomType = listOf("A", "B", "C", "D", "E").random()

    override suspend fun fetchPostWithAuthorId(
        authorId: String,
        limit: Long
    ): Result<List<PostModel>> {
        return runCatching {
            postCollection
                .whereEqualTo("authorId", authorId)
                .limit(limit)
                .orderBy("registerAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .map { it.toObject(PostDto::class.java).toPostModel(randomType) }
        }
    }

    override suspend fun uploadPost(
        authorId: String,
        description: String,
        imageUrl: String,
        imageRatio: Float,
        tags: List<String>
    ): Result<String> {
        return runCatching {
            val upload = PostDto(
                authorId = authorId,
                description = description,
                imageUrl = imageUrl,
                imageRatio = imageRatio,
                tags = tags,
                registerAt = PorringDateTime.getNowDateTimeUTCString(),
            )
            postCollection.add(upload).await().let { documentReference ->
                documentReference.update("postId", "post-${documentReference.id}")
                "post-${documentReference.id}"
            }
        }
    }

    override suspend fun updateImageUrl(documentId: String, imageUrl: String) {
        postCollection.document(documentId).update("imageUrl", imageUrl)
    }

    override suspend fun fetchRandomPost(
        uid: String,
        count: Int,
        randomType: String
    ): Result<List<PostModel>> {
        return kotlin.runCatching {
            this.randomType = randomType

            val randomValue = (0..Long.MAX_VALUE).random()
            val queryDirection =
                listOf(Query.Direction.ASCENDING, Query.Direction.DESCENDING).random()

            val query = postCollection
                .whereNotEqualTo("authorId", uid)
                .whereGreaterThan("random${this.randomType}", randomValue)
                .orderBy("random${this.randomType}", queryDirection)
                .limit(count.toLong())

            val initialPosts = query.get().await()
            val resultPosts = if (initialPosts.size() < count) {
                val remainingCount = count - initialPosts.size()
                val fallbackQuery = when (queryDirection) {
                    Query.Direction.ASCENDING -> {
                        postCollection
                            .whereNotEqualTo("authorId", uid)
                            .whereGreaterThan("random${this.randomType}", 0)
                            .orderBy("random${this.randomType}", queryDirection)
                            .limit(remainingCount.toLong())
                    }

                    Query.Direction.DESCENDING -> {
                        postCollection
                            .whereNotEqualTo("authorId", uid)
                            .whereLessThan("random${this.randomType}", Long.MAX_VALUE)
                            .orderBy("random${this.randomType}", queryDirection)
                            .limit(remainingCount.toLong())
                    }
                }.get().await()

                initialPosts + fallbackQuery
            } else {
                initialPosts
            }

            resultPosts.map {
                it.toObject(PostDto::class.java)
                    .toPostModel(randomType)
            }
        }
    }

    override suspend fun getPostBySearch(
        currentUserId: String,
        postIds: List<String>,
        key: String?,
        perPage: Long
    ): Result<List<PostModel>> {
        return runCatching {
            if (postIds.isEmpty()) return@runCatching emptyList()

            val chunkSize = 10 // Firestore whereIn 안전 한계
            val allPosts = mutableListOf<PostModel>()

            // chunk를 나눠서 호출(느려질 수는 있으나 안전)
            postIds.chunked(chunkSize).forEach { chunk ->
                val snapshot = postCollection
                    .whereNotEqualTo("authorId", currentUserId)
                    .whereIn("postId", chunk)
                    .get()
                    .await()

                val posts = snapshot.mapNotNull {
                    it.toObject(PostDto::class.java).toPostModel(randomType)
                }

                allPosts.addAll(posts)
            }

            val sorted = allPosts.sortedBy { it.registerAt }

            val paginated = if (key == null) {
                sorted.take(perPage.toInt())
            } else {
                sorted.dropWhile { it.postId <= key }
                    .take(perPage.toInt())
            }

            paginated
        }
    }

    override fun setPostReaction(postId: String, reaction: Int, prevReaction: Int?) {
        val id = postId.substringAfter("-")
        val postRef = postCollection.document(id)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val prevCount = snapshot.get("reactionCount") as? List<Long> ?: List(6) { 0L }
            val newCount = prevCount.toMutableList()

            prevReaction?.let { newCount[prevReaction] = newCount[prevReaction] - 1L }
            newCount[reaction] = newCount[reaction] + 1L
            transaction.update(postRef, "reactionCount", newCount)
        }
    }

    override fun deletePostReaction(postId: String, reaction: Int) {
        val id = postId.substringAfter("-")
        val postRef = postCollection.document(id)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val prevCount = snapshot.get("reactionCount") as? List<Long> ?: List(6) { 0L }
            val newCount = prevCount.toMutableList()

            newCount[reaction] = (newCount[reaction] - 1L).coerceAtLeast(0L)
            transaction.update(postRef, "reactionCount", newCount)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return kotlin.runCatching {
            val documentId = postId.substringAfter("-")

            postCollection.document(documentId).delete().await()
        }
    }

    override suspend fun getAllPostsByAuthorId(authorId: String): Result<List<PostModel>> {
        return kotlin.runCatching {
            postCollection
                .whereEqualTo("authorId", authorId)
                .orderBy("registerAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .mapNotNull { it.toObject(PostDto::class.java).toPostModel(randomType) }
        }
    }
}
