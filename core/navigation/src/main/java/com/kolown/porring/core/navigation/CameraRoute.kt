package com.kolown.porring.core.navigation

import com.kolown.porring.core.model.UploadModel
import kotlinx.serialization.Serializable

sealed interface CameraRoute: Route {

    @Serializable
    data object Camera : CameraRoute

    @Serializable
    data class ImageEdit(val imgUri: String) : CameraRoute

    @Serializable
    data class Upload(val imgUri: String, val imageRatio: Float, val uploadModel: UploadModel) : CameraRoute
}