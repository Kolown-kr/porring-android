package com.kolown.porring.core.network

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
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
                val docRef = tagCollection.document(tagName)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(docRef)

                    if (!snapshot.exists()) {
                        val newTag = mapOf(
                            "tagName" to tagName,
                            "postIds" to listOf(postId)
                        )
                        transaction.set(docRef, newTag)
                    } else {
                        transaction.update(docRef, "postIds", FieldValue.arrayUnion(postId))
                    }
                }.await()
            }
        }
    }

    override suspend fun getPostIdsByTagName(tagName: String): Result<List<String>> {
        return runCatching {
            val snapshot = tagCollection.document(tagName).get().await()
            if (!snapshot.exists()) {
                emptyList()
            } else {
                snapshot.get("postIds")
                    ?.let { it as? List<*> }
                    ?.filterIsInstance<String>()
                    ?: emptyList()
            }
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
                .let { q ->
                    if (key != null) q.startAfter(key) else q
                }
                .limit(perPage)

            val documents = query.get().await()
            for (doc in documents) {
                val tagName = doc.id
                val postIds = doc.get("postIds") as? List<*> ?: emptyList<Any>()

                if (postIds.isNotEmpty()) {
                    tags.add(Tag(name = tagName))
                }
            }

            tags
        }
    }
}
