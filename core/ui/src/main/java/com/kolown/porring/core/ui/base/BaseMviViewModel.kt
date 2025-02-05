package com.kolown.porring.core.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.ui.BuildConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseMviViewModel<S: UiState, I: UiIntent, E: UiSideEffect>(initialState: S) : ViewModel() {
    private val logTag = javaClass.simpleName

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val sideEffectChannel = Channel<E>(Channel.UNLIMITED)
    val sideEffect = sideEffectChannel.receiveAsFlow()

    abstract fun handleIntent(intent: I)

    protected inline fun launch(crossinline block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    protected fun reduce(
        block: S.() -> S
    ) {
        _uiState.update(block)
    }

    protected fun postSideEffect(sideEffect: E) {
        launch {
            sideEffectChannel.send(sideEffect)
        }
    }

    protected fun <T> Flow<T>.debugLog(subject: String): Flow<T> =
        if (BuildConfig.DEBUG) {
            onEach { Log.d(logTag, "$subject: $it") }
        } else {
            this
        }

    protected fun <T> T.debugLog(subject: String): T =
        if (BuildConfig.DEBUG) {
            also { Log.d(logTag, "$subject: $this") }
        } else {
            this
        }
}