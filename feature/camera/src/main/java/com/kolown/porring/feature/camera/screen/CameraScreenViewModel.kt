package com.kolown.porring.feature.camera.screen

import android.graphics.Bitmap
import android.net.Uri
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
            val bitmap = imageGenerateRepository.decodeSampledBitmapFromUri(uri)
            _uri.value = imageGenerateRepository.saveBitmapToCache(bitmap!!)
        }
//        TODO 이미지 편집 화면 나오면 그냥 uri 넘겨도 됨.(어차피 이미지 편집 화면에서 크롭할 예정)
//        _uri.value = uri
    }

    fun saveBitmapToCache(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _uri.value = imageGenerateRepository.saveBitmapToCache(bitmap)
        }
    }
}
