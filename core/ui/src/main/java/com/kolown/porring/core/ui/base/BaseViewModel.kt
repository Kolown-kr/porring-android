package com.kolown.porring.core.ui.base

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kolown.porring.core.ui.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<STATE>(
    initialState: STATE,
) : ViewModel() {

    private val logTag by lazy(PUBLICATION) {
        (this::class.java.simpleName).let { tag: String ->
            if (tag.length <= 23 || Build.VERSION.SDK_INT >= 26) {
                tag
            } else {
                tag.take(23)
            }
        }
    }

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _error = Channel<Throwable>(Channel.UNLIMITED)
    val error = _error.receiveAsFlow()

    protected val ceh = CoroutineExceptionHandler { _, throwable ->
        Log.e(logTag, "Unhandled exception", throwable)
        viewModelScope.launch {
            _error.send(throwable)
        }
    }

    protected inline fun launch(
        coroutineContext: CoroutineContext = ceh,
        crossinline action: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(coroutineContext) {
            action(this)
        }
    }

    protected fun updateState(action: STATE.() -> STATE) {
        _state.update(action)
    }

    protected fun <T> Flow<T>.debugLog(name: String): Flow<T> =
        if (BuildConfig.DEBUG) {
            onEach { Log.d(logTag, "$name: $it") }
        } else this

    protected fun <T> T.debugLog(name: String? = null): T =
        if (BuildConfig.DEBUG) {
            this.also { Log.d(logTag, "${name.orEmpty()} : $this") }
        } else this

}