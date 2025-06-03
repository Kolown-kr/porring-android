package com.kolown.porring.feature.imageedit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.ImageGenerateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val repository: ImageGenerateRepository
) : ViewModel() {
    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri: StateFlow<String?> = _imageUri

    fun cropImage(
        imageUri: String,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        boxSize: Size,
        imageSize: Size
    ) {
        viewModelScope.launch {
            val editedUri = repository.saveEditedImage(
                imageUri,
                scale,
                offsetX,
                offsetY,
                boxSize.width,
                boxSize.height,
                imageSize.width,
                imageSize.height
            )

            _imageUri.value = editedUri
        }
    }
}