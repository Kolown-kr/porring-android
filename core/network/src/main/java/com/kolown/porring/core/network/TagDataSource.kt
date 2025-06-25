package com.kolown.porring.core.network

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.kolown.porring.core.model.Tag
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface TagDataSource {
    suspend fun uploadTags(tags: List<String>, postId: String): Result<Unit>
    suspend fun getPostIdsByTagName(tagName: String): Result<List<String>>
    suspend fun getTagBySearch(searchText: String, key: String?, perPage: Long): Result<List<Tag>>
}

class TagDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TagDataSource {
    private val tagCollection = firestore.collection("tag")

    override suspend fun uploadTags(tags: List<String>, postId: String): Result<Unit> {
        return runCatching {
            for (tagName in tags) {
                val tagRef = tagCollection.document(tagName)
                val postIdsRef = tagRef.collection("postIds").document(postId)

                firestore.runTransaction { transaction ->
                    val tagSnapshot = transaction.get(tagRef)
                    val postSnapshot = transaction.get(postIdsRef)

                    if (!tagSnapshot.exists()) {
                        transaction.set(tagRef, mapOf("tagName" to tagName))
                    }

                    if (!postSnapshot.exists()) {
                        transaction.set(postIdsRef, mapOf(postId to postId))
                    }
                }.await()
            }
        }
    }

    override suspend fun getPostIdsByTagName(tagName: String): Result<List<String>> {
        return runCatching {
            val snapshot = tagCollection
                .document(tagName)
                .collection("postIds")
                .get()
                .await()

            snapshot.documents.map { it.id }
        }
    }

    override suspend fun getTagBySearch(
        searchText: String,
        key: String?,
        perPage: Long
    ): Result<List<Tag>> {
        return runCatching {
            val tags = mutableListOf<Tag>()
            val query = tagCollection
                .orderBy(FieldPath.documentId())
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .let { if (key != null) it.startAfter(key) else it }
                .limit(perPage)

            val documents = query.get().await()

            for (doc in documents) {
                val tagName = doc.id
                val postRef = doc.reference.collection("postIds").limit(1).get().await()

                if (!postRef.isEmpty) {
                    tags.add(Tag(name = tagName))
                }
            }

            tags
        }
    }
}
