package com.kolown.porring.core.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.network.model.PostDto
import com.kolown.porring.core.network.model.toPostModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PostDataSource {
    suspend fun uploadPost(authorId: String, description: String): Result<String>
    suspend fun updateImageUrl(documentId: String, imageUrl: String)
    suspend fun getRandomPost(uid: String, page: Long, perPage: Long): Result<List<PostModel>>
    suspend fun getRandomPost(
        uid: String,
        count: Int,
        randomType: String
    ): Result<List<PostModel>>

    suspend fun getUserPost(uid: String, perPage: Long): Result<List<PostModel>>
    suspend fun getPostBySearch(
        currentUserId: String,
        postIds: List<String>,
        key: String?,
        perPage: Long
    ): Result<List<PostModel>>

    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun fetchPostWithAuthorId(authorId: String, limit: Long): Result<List<PostModel>>
    fun resetLastVisible()
    fun setPostReaction(userId: String, postId: String, reaction: Reactions)
    fun deletePostReaction(userId: String, postId: String)
}

class PostDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : PostDataSource {
    private val postCollection = firestore.collection("post")
    private var randomType = listOf("A", "B", "C", "D", "E").random()
    private var lastVisible: DocumentSnapshot? = null

    override fun resetLastVisible() {
        lastVisible = null
    }

    override suspend fun getUserPost(uid: String, perPage: Long): Result<List<PostModel>> {
        return runCatching {
            if (lastVisible == null) {
                postCollection
                    .whereEqualTo("authorId", uid)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .limit(perPage)
                    .get()
                    .await()
                    .also { querySnapshot -> lastVisible = querySnapshot.documents.lastOrNull() }
                    .mapNotNull { it.toObject(PostDto::class.java).toPostModel() }
            } else {
                postCollection
                    .whereEqualTo("authorId", uid)
                    .orderBy("registerAt", Query.Direction.DESCENDING)
                    .startAfter(lastVisible!!)
                    .limit(perPage)
                    .get()
                    .await()
                    .also { querySnapshot -> lastVisible = querySnapshot.documents.lastOrNull() }
                    .mapNotNull { it.toObject(PostDto::class.java).toPostModel() }
            }
        }
    }

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
                .map { it.toObject(PostDto::class.java).toPostModel() }

        }
    }

    override suspend fun uploadPost(
        authorId: String,
        description: String
    ): Result<String> {
        return runCatching {
            val upload = PostDto(
                authorId = authorId,
                description = description,
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

    override suspend fun getRandomPost(
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

            coroutineScope {
                resultPosts.map { querySnapshot ->
                    async {
                        val reactionQuery = postCollection
                            .document(querySnapshot.id)
                            .collection("reactions")
                            .get()
                            .await()
                        val reactions = reactionQuery
                            .mapNotNull { it.getLong("reaction")?.toInt() }
                            .distinct()
                        val myReaction = reactionQuery
                            .find { it.id == uid }
                            ?.getLong("reaction")
                            ?.toInt()

                        querySnapshot.toObject(PostDto::class.java)
                            .copy(
                                reactions = reactions,
                                myReaction = myReaction
                            )
                            .toPostModel(randomType)
                    }
                }.awaitAll()
            }
        }
    }

    override suspend fun getRandomPost(
        uid: String,
        page: Long,
        perPage: Long
    ): Result<List<PostModel>> {
        return runCatching {
            val fetchPosts: suspend (Long) -> List<PostModel> = { key ->
                postCollection
                    .whereNotEqualTo("authorId", uid)
                    .whereGreaterThan("random$randomType", key)
                    .orderBy("random$randomType", Query.Direction.ASCENDING)
                    .limit(perPage)
                    .get()
                    .await()
                    .map { it.toObject(PostDto::class.java).toPostModel(randomType) }
            }

            fetchPosts(page).ifEmpty { fetchPosts(0) }
        }
    }

    override suspend fun getPostBySearch(
        currentUserId: String,
        postIds: List<String>,
        key: String?,
        perPage: Long
    ): Result<List<PostModel>> {
        return runCatching {
            // 쿼리 초기화
            val query = postCollection
                .whereIn("postId", postIds)
                .orderBy("postId", Query.Direction.ASCENDING)
                .let { if (key != null) it.startAfter(key) else it }
                .limit(perPage)
                .get()
                .await()
                .mapNotNull { it.toObject(PostDto::class.java).toPostModel(randomType) }

            query
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return runCatching {
            val documentId = postId.substringAfter("-")
            postCollection.document(documentId).delete().await()
        }
    }

    override fun setPostReaction(userId: String, postId: String, reaction: Reactions) {
        val id = postId.substringAfter("-")
        val reactionsCollection = postCollection.document(id).collection("reactions")

        reactionsCollection.document(userId).set(mapOf("reaction" to reaction.value))
    }

    override fun deletePostReaction(postId: String, userId: String) {
        val id = postId.substringAfter("-")
        val reactionsCollection = postCollection.document(id).collection("reactions")

        reactionsCollection.document(userId).delete()
    }
}
