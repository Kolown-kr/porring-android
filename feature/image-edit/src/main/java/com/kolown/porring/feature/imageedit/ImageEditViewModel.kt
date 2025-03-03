package com.kolown.porring.feature.imageedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.ImageGenerateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val repository: ImageGenerateRepository
): ViewModel() {
    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri: StateFlow<String?> = _imageUri

    fun cropImage(
        imageUri: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        cropRatio: Float
    ) {
        viewModelScope.launch {
            val editedUri = repository.saveEditedImage(
                imageUri,
                scale,
                offsetX,
                offsetY,
                cropRatio
            )

            _imageUri.value = editedUri
        }
    }

    fun getBoundedOffset(scale: Float, offset: Float, boxSize: Float, imageSize: Float): Float {
        val maxOffset = maxOf(((imageSize * scale) - boxSize) / 2, 0f)
        return offset.coerceIn(-maxOffset, maxOffset)
    }
}