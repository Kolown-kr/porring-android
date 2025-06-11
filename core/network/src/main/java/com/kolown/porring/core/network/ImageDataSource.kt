package com.kolown.porring.core.network

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

interface ImageDataSource {
    suspend fun getImageUrl(authorId: String, fileUri: Uri): Result<String>
}

class ImageDataSourceImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageDataSource {
    override suspend fun getImageUrl(authorId: String, fileUri: Uri): Result<String> {
        return runCatching {
            storage.reference.child(authorId.toRefName()).let { imgRef ->
                imgRef.putFile(fileUri).await()
                imgRef.downloadUrl.await().toString()
            }
        }
    }

    private fun String.toRefName(): String {
        val timeStamp = PorringDateTime.getNowDateTimeString()

        return (this + timeStamp).toHash()
    }

    private fun String.toHash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(this.toByteArray())

        return hash.joinToString("") { "%02x".format(it) }
    }

}
