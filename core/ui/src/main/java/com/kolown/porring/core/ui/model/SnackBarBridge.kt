package com.kolown.porring.core.ui.component

import com.kolown.porring.core.model.SnackBarEvent

class SnackBarBridge(
    private val onSnackBarDataAdded: (SnackBarEvent) -> Unit
) {
    fun postSnackBarEvent(event: SnackBarEvent) {
        onSnackBarDataAdded(event)
    }

    fun postSnackBarString(message: String) {
        onSnackBarDataAdded(SnackBarEvent.Message(message, null))
    }
}
