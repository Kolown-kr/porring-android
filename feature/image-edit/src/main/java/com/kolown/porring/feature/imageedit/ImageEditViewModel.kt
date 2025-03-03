package com.kolown.porring.feature.imageedit

import androidx.lifecycle.ViewModel
import com.kolown.porring.core.data.repository.ImageGenerateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val repository: ImageGenerateRepository
): ViewModel() {

}