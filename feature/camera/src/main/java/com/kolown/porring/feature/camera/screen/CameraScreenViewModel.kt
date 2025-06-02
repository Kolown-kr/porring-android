package com.kolown.porring.feature.camera.screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.ImageGenerateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val imageGenerateRepository: ImageGenerateRepository,
) : ViewModel() {

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri: StateFlow<Uri?> = _uri.asStateFlow()

    fun setUri(uri: Uri) {
        viewModelScope.launch {
            val bitmap = imageGenerateRepository.decodeSampledBitmapFromUri(
                uri = uri,
                resizeNeeded = true,
                rotateNeeded = true
            )
            _uri.value = imageGenerateRepository.saveBitmapToCache(bitmap!!)
        }
    }

    fun saveBitmapToCache(bitmap: Bitmap) {
        viewModelScope.launch {
            val resizedBitmap = imageGenerateRepository.resizeBitmap(bitmap)
            _uri.value = imageGenerateRepository.saveBitmapToCache(resizedBitmap)
        }
    }
}
