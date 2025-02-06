package com.kolown.porring.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

interface ImageGenerateRepository {
    suspend fun saveBitmapToCache(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): Uri?

    suspend fun decodeSampledBitmapFromUri(uri: Uri): Bitmap?
    suspend fun getImageRatio(uri: String): Float
}

class ImageGenerateRepositoryImpl(
    private val applicationContext: Context
) : ImageGenerateRepository {
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

    override suspend fun decodeSampledBitmapFromUri(
        uri: Uri,
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
        options.inSampleSize = calculateInSampleSize(options)
        options.inJustDecodeBounds = false

        return applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }?.let { originalBitmap ->
            rotateAndCropBitmap(originalBitmap, uri)
        }
    }

    override suspend fun getImageRatio(uri: String): Float {
        return applicationContext.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@use null
            if (bitmap.width > bitmap.height) {
                5f / 4f
            } else {
                4f / 5f
            }
        } ?: (4f / 5f)
    }

    private fun rotateAndCropBitmap(originalBitmap: Bitmap, uri: Uri): Bitmap {
        val exif = try {
            applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                ExifInterface(inputStream)
            }
        } catch (e: IOException) {
            Log.e("회전 에러", e.message.toString())
            null
        }

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        val rotatedBitmap = rotateBitmap(orientation, originalBitmap)
        return cropToAspectRatio(rotatedBitmap)
    }

    private fun rotateBitmap(orientation: Int, source: Bitmap): Bitmap {
        val angle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_NORMAL -> 0f
            else -> 0f
        }
        val matrix = Matrix().apply {
            postRotate(angle)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > 900 || width > 720) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= 900 && halfWidth / inSampleSize >= 720) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun cropToAspectRatio(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val targetRatio = 4f / 5f

        var targetWidth = originalWidth
        var targetHeight = originalHeight

        if (originalWidth > originalHeight) {
            targetWidth = (originalHeight / targetRatio).toInt()
        } else {
            targetHeight = (originalWidth / targetRatio).toInt()
        }

        val cropWidth = min(originalWidth, targetWidth)
        val cropHeight = min(originalHeight, targetHeight)

        val xOffset = (originalWidth - cropWidth) / 2
        val yOffset = (originalHeight - cropHeight) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, cropWidth, cropHeight)
    }
}