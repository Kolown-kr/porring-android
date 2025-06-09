package com.kolown.porring.feature.upload

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kolown.porring.core.common.retry
import com.kolown.porring.core.data.repository.ImageRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.UploadModel
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.feature.upload.navigation.UploadType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val postRepository: PostRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uploadState = MutableStateFlow(UploadModel())
    val uploadState = _uploadState.asStateFlow()

    private val _uploadImage = MutableStateFlow("")
    val uploadImage = _uploadImage.asStateFlow()

    private val _uploadAttempted = MutableStateFlow(false)
    val uploadAttempted = _uploadAttempted.asStateFlow()

    val uploadEnable = combine(uploadState, uploadImage) { state, imageUrl ->
        state.description.isNotBlank() &&
                state.categoryItems.isNotEmpty() &&
                state.imgUri.isNotBlank() &&
                imageUrl.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        _uploadState.value = extractUploadModel()
    }

    fun changeDescription(description: String) {
        _uploadState.update { it.copy(description = description) }
    }

    fun addCategory() {
        _uploadState.update { it.copy(categoryItems = it.categoryItems + "") }
    }

    fun removeCategory(name: String) {
        _uploadState.update { model ->
            model.copy(categoryItems = model.categoryItems.filterNot { it == name })
        }
    }

    fun changeCategoryName(index: Int, name: String) {
        _uploadState.update { state ->
            val newCategories = state.categoryItems.toMutableList().apply {
                if (index in indices) this[index] = name
            }
            state.copy(categoryItems = newCategories)
        }
    }

    fun getUriWebP(uri: String) {
        viewModelScope.launch {
            val bitmap = retry(times = 3) {
                repository.decodeSampledBitmapFromUri(
                    uri = Uri.parse(uri),
                    resizeNeeded = true,
                    rotateNeeded = true
                ) ?: throw IOException("Decode Failed")
            }

            val webPUri = repository.saveBitmapToCache(
                bitmap,
                Bitmap.CompressFormat.WEBP,
                80
            ) ?: return@launch

            _uploadState.update {
                it.copy(
                    imgUri = webPUri.toString()
                )
            }

            repository.getImageUrl(fileUri = webPUri)
                .onSuccess {
                    _uploadImage.value = it
                    _uploadAttempted.value = true
                }
                .onFailure {
                    _uploadImage.value = ""
                    _uploadAttempted.value = true
                }
        }
    }

    fun uploadPost() {
        postRepository.uploadPost(
            fileUri = uploadImage.value,
            description = uploadState.value.description,
            tags = uploadState.value.categoryItems
        )
    }

    private fun extractUploadModel(): UploadModel {
        return savedStateHandle.toRoute<Route.Upload>(mapOf(typeOf<UploadModel>() to UploadType)).uploadModel
    }
}