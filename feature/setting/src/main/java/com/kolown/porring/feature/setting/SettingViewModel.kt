package com.kolown.porring.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val followRepository: FollowRepository
) : ViewModel() {
    private var _logoutEnd = MutableSharedFlow<Boolean>()
    val logoutEnd = _logoutEnd.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()

            if (result.isSuccess) {
                _logoutEnd.emit(true)
                followRepository.clearFollowCache()
            } else {
                _logoutEnd.emit(false)
            }
        }
    }
}