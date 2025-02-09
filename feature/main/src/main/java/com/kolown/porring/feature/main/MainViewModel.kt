package com.kolown.porring.feature.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.RemoteConfigRepository
import com.kolown.porring.core.model.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private val _versionNameFlow = MutableSharedFlow<String>()

    val versionNameFlow = _versionNameFlow.asSharedFlow()

    private var _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<SnackBarEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    fun getVersionName() {
        viewModelScope.launch {
            remoteConfigRepository.getVersionName()?.let {
                _versionNameFlow.emit(it)
            }
        }
    }

    fun postSnackBarData(data: SnackBarEvent) {
        viewModelScope.launch {
            _snackBarFlow.emit(data)
        }
    }

}
