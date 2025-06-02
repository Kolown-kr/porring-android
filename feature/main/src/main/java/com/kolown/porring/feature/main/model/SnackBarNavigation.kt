package com.kolown.porring.feature.main.model

import com.kolown.porring.core.model.UploadModel

sealed class SnackBarNavigation {
    data object ToGallery : SnackBarNavigation()
    data class ToUpload (val uploadModel: UploadModel) : SnackBarNavigation()
}