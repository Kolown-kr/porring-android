package com.kolown.porring.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.kolown.porring.core.data.utils.Constants.IMAGE_LONG
import com.kolown.porring.core.data.utils.Constants.IMAGE_RATIO
import com.kolown.porring.core.data.utils.Constants.IMAGE_SHORT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun decodeSampledBitmapFromUri(uri: Uri, rotateNeeded: Boolean): Bitmap?
    suspend fun resizeBitmap(bitmap: Bitmap): Bitmap
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
        rotateNeeded: Boolean
    ): Bitmap? = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight)
        options.inJustDecodeBounds = false

        applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }?.let { originalBitmap ->
            if(rotateNeeded) {
                rotateAndCropBitmap(originalBitmap, uri)
            } else {
                originalBitmap
            }
        }
    }

    override suspend fun resizeBitmap(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val inSampleSize = calculateInSampleSize(bitmap.width, bitmap.height)

        val newWidth = bitmap.width / inSampleSize
        val newHeight = bitmap.height / inSampleSize

        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private suspend fun rotateAndCropBitmap(originalBitmap: Bitmap, uri: Uri): Bitmap =
        withContext(Dispatchers.Default) {
            val orientation = getExifOrientation(uri)
            val matrix = getRotationMatrix(orientation)

            val originalWidth = originalBitmap.width
            val originalHeight = originalBitmap.height
            val (cropWidth, cropHeight) = calculateCropSize(originalWidth, originalHeight)

            val xOffset = (originalWidth - cropWidth) / 2
            val yOffset = (originalHeight - cropHeight) / 2

            Bitmap.createBitmap(
                originalBitmap,
                xOffset,
                yOffset,
                cropWidth,
                cropHeight,
                matrix,
                true
            )
        }

    private suspend fun getExifOrientation(uri: Uri): Int = withContext(Dispatchers.IO) {
        try {
            applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: IOException) {
            Log.e("Exif 오류", e.message.toString())
            ExifInterface.ORIENTATION_NORMAL
        }
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

    private fun calculateCropSize(originalWidth: Int, originalHeight: Int): Pair<Int, Int> {
        return if (originalWidth > originalHeight) {
            val targetWidth = (originalHeight / IMAGE_RATIO).toInt()
            Pair(min(originalWidth, targetWidth), originalHeight)
        } else {
            val targetHeight = (originalWidth / IMAGE_RATIO).toInt()
            Pair(originalWidth, min(originalHeight, targetHeight))
        }
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