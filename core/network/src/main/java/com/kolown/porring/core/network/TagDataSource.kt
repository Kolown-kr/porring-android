package com.kolown.porring.core.network

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.model.TagModel
import com.kolown.porring.core.network.model.TagDto
import com.kolown.porring.core.network.model.toTagModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface TagDataSource {
    suspend fun uploadTags(tags: List<String>, postId: String): Result<Unit>
    suspend fun getPostTagByTagId(tagId: String): Result<List<String>>
    suspend fun getTagBySearch(searchText: String,key:String?,perPage:Long) : Result<List<Tag>>
}

class TagDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TagDataSource {
    private val postTagCollection = firestore.collection("postTag")
    private val tagCollection = firestore.collection("tag")

    override suspend fun uploadTags(tags: List<String>, postId: String): Result<Unit> {
        return runCatching {
            for (tagName in tags) {
                val docRef = tagCollection.document(tagName)

                firestore.runTransaction {  transaction ->
                    val snapshot = transaction.get(docRef)

                    if(!snapshot.exists()) {
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

    override suspend fun getPostTagByTagId(tagId: String): Result<List<String>> {
        return runCatching {
            val postIds = postTagCollection
                .whereEqualTo("tagId", tagId)
                .get()
                .await()
                .map { it.data["postId"].toString() }
            postIds
        }
    }

    override suspend fun getTagBySearch(searchText: String, key: String?, perPage: Long): Result<List<Tag>> {
        return kotlin.runCatching {
            val tags = mutableListOf<Tag>()
            val documents = if (key == null) {
                tagCollection.whereGreaterThanOrEqualTo("tagName", searchText)
                    .whereLessThanOrEqualTo("tagName", searchText + "\uf8ff")
                    .limit(SEARCH_TAG_PER_PAGE.toLong())
                    .get()
                    .await()
            } else {
                tagCollection.whereGreaterThan("tagName", key)
                    .whereGreaterThanOrEqualTo("tagName", searchText)
                    .whereLessThanOrEqualTo("tagName", searchText + "\uf8ff")
                    .limit(SEARCH_TAG_PER_PAGE.toLong())
                    .get()
                    .await()
            }

            for (document in documents) {
                val tagName = document.getString("tagName")
                val tagId = document.getString("tagId")
                if (tagName != null && tagId != null) {
                    val existsInPostTags = postTagCollection.whereEqualTo("tagId", tagId)
                        .get()
                        .await()
                        .isEmpty

                    if (!existsInPostTags) {
                        tags.add(Tag(tagId, tagName))
                    }
                }
            }
            tags.toList()
        }
    }

    companion object {
        const val SEARCH_TAG_PER_PAGE = 5
    }
}
