package com.kolown.porring.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private var _logoutEnd = MutableSharedFlow<Boolean>()
    val logoutEnd = _logoutEnd.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    _logoutEnd.emit(true)
                    // TODO: UseCase생성 후 통합처리 하도록 이전해야 함
                    followRepository.clearFollowCache()
                    userRepository.clearReactedPostCache()
                    postRepository.clearMyPostCache()
                }
                .onFailure { _logoutEnd.emit(false) }
        }
    }
}