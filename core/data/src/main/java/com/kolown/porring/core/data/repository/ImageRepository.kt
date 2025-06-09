package com.kolown.porring.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.kolown.porring.core.data.utils.Constants.IMAGE_LONG
import com.kolown.porring.core.data.utils.Constants.IMAGE_SHORT
import com.kolown.porring.core.data.utils.retryWithLimit
import com.kolown.porring.core.network.AuthDataSource
import com.kolown.porring.core.network.ImageDataSource
import com.kolown.porring.core.network.PostDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

interface ImageRepository {
    suspend fun getImageUrl(fileUri: Uri): Result<String>
    suspend fun saveBitmapToCache(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): Uri?

    suspend fun saveEditedImage(
        imageUri: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        cropWidth: Float,
        cropHeight: Float,
        imageWidth: Float,
        imageHeight: Float
    ): String?

    suspend fun decodeSampledBitmapFromUri(
        uri: Uri,
        resizeNeeded: Boolean = false,
        rotateNeeded: Boolean
    ): Bitmap?

    suspend fun resizeBitmap(bitmap: Bitmap): Bitmap
}

class ImageRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val imageDataSource: ImageDataSource,
    @Named("google") private val googleAuthDataSource: AuthDataSource,
) : ImageRepository {
    override suspend fun getImageUrl(fileUri: Uri): Result<String> =
        withContext(Dispatchers.IO) {
            val authorId = googleAuthDataSource.getUserId()

            retryWithLimit {
                imageDataSource.getImageUrl(authorId, fileUri).getOrElse {
                    throw IOException(it)
                }
            }
        }

    override suspend fun saveBitmapToCache(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Uri? {
        return try {
            val file = File(applicationContext.cacheDir, "photo_${System.currentTimeMillis()}.jpg")

            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(format, quality, outputStream)
                }
            }

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun saveEditedImage(
        imageUri: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        cropWidth: Float,
        cropHeight: Float,
        imageWidth: Float,
        imageHeight: Float
    ): String? {
        val originalBitmap = decodeSampledBitmapFromUri(
            uri = Uri.parse(imageUri),
            resizeNeeded = false,
            rotateNeeded = false
        ) ?: return null

        val croppedBitmap =
            cropBitmap(
                originalBitmap,
                scale,
                offsetX,
                offsetY,
                cropWidth,
                cropHeight,
                imageWidth,
                imageHeight
            )

        return saveBitmapToCache(croppedBitmap)?.toString()
    }

    override suspend fun decodeSampledBitmapFromUri(
        uri: Uri,
        resizeNeeded: Boolean,
        rotateNeeded: Boolean
    ): Bitmap? {
        val byteArray = withContext(Dispatchers.IO) {
            applicationContext.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }
        } ?: return null

        return withContext(Dispatchers.Default) {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

            if (resizeNeeded) {
                options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight)
            }
            options.inJustDecodeBounds = false

            val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
                ?: return@withContext null

            if (rotateNeeded) {
                val exifOrientation = ExifInterface(ByteArrayInputStream(byteArray))
                    .getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                rotateBitmap(decodedBitmap, exifOrientation)
            } else {
                decodedBitmap
            }
        }
    }

    override suspend fun resizeBitmap(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val inSampleSize = calculateInSampleSize(bitmap.width, bitmap.height)

        val newWidth = bitmap.width / inSampleSize
        val newHeight = bitmap.height / inSampleSize

        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private suspend fun rotateBitmap(originalBitmap: Bitmap, orientation: Int): Bitmap =
        withContext(Dispatchers.Default) {
            val matrix = getRotationMatrix(orientation)

            Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )
        }

    private fun getRotationMatrix(orientation: Int): Matrix {
        val angle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_NORMAL -> 0f
            else -> 0f
        }
        return Matrix().apply { postRotate(angle) }
    }

    private suspend fun cropBitmap(
        bitmap: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        boxWidth: Float,
        boxHeight: Float,
        imageWidth: Float,
        imageHeight: Float
    ): Bitmap = withContext(Dispatchers.Default) {
        val originalWidth = bitmap.width.toFloat()
        val originalHeight = bitmap.height.toFloat()

        val widthRatio = originalWidth / imageWidth
        val heightRatio = originalHeight / imageHeight

        val cropWidth = boxWidth * widthRatio / scale
        val cropHeight = boxHeight * heightRatio / scale

        val adjustedOffsetX = offsetX * widthRatio / scale
        val adjustedOffsetY = offsetY * heightRatio / scale

        val left =
            ((originalWidth - cropWidth) / 2 - adjustedOffsetX).coerceIn(0f, originalWidth).toInt()
        val top = ((originalHeight - cropHeight) / 2 - adjustedOffsetY).coerceIn(0f, originalHeight)
            .toInt()
        val right = (left + cropWidth).coerceIn(0f, originalWidth).toInt()
        val bottom = (top + cropHeight).coerceIn(0f, originalHeight).toInt()

        Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    private fun calculateInSampleSize(width: Int, height: Int): Int {
        var inSampleSize = 1
        val maxSize = maxOf(height, width)
        val minSize = minOf(height, width)

        if (maxSize > IMAGE_LONG || minSize > IMAGE_SHORT) {
            val halfMax = maxSize / 2
            val halfMin = minSize / 2

            while (halfMax / inSampleSize >= IMAGE_LONG && halfMin / inSampleSize >= IMAGE_SHORT) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}