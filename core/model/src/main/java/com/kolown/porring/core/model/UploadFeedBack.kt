package com.kolown.porring.core.model

sealed class UploadFeedBack {
    data object Uploading : UploadFeedBack()
    data object Success : UploadFeedBack()
    data class Error(val uploadModel: UploadModel) : UploadFeedBack()
}