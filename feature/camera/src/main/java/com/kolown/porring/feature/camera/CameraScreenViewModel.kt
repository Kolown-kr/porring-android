package com.kolown.porring.feature.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri: StateFlow<Uri?> = _uri.asStateFlow()

    fun setUri(uri: Uri) {
        viewModelScope.launch {
            val bitmap = imageRepository.decodeSampledBitmapFromUri(
                uri = uri,
                resizeNeeded = true,
                rotateNeeded = true
            )
            _uri.value = imageRepository.saveBitmapToCache(bitmap!!)
        }
    }

    fun saveBitmapToCache(bitmap: Bitmap) {
        viewModelScope.launch {
            val resizedBitmap = imageRepository.resizeBitmap(bitmap)
            _uri.value = imageRepository.saveBitmapToCache(resizedBitmap)
        }
    }
}
