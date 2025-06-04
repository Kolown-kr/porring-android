package com.kolown.porring.feature.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.RemoteConfigRepository
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.model.UploadFeedBack
import com.kolown.porring.feature.main.model.SnackBarNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    postRepository: PostRepository,
    authRepository: AuthRepository,
    followRepository: FollowRepository,
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {
    val loginState = authRepository.checkUserLoggedIn()

    init {
        viewModelScope.launch { postRepository.fetchMyPosts() }
        loginState.onEach {
            if (it) followRepository.fetchFollows()
        }.launchIn(viewModelScope)
    }

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private val _versionNameFlow = MutableSharedFlow<String>()
    val versionNameFlow = _versionNameFlow.asSharedFlow()

    private val _snackBarFlow = MutableSharedFlow<SnackBarEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val snackBarFlow = _snackBarFlow.asSharedFlow()
    private val _navigationRequest = MutableSharedFlow<SnackBarNavigation>()
    val navigationRequest: SharedFlow<SnackBarNavigation> = _navigationRequest.asSharedFlow()


    init {
        viewModelScope.launch {
            postRepository.getUploadFeedBack().collect { feedback ->
                val event = when (feedback) {
                    UploadFeedBack.Uploading -> SnackBarEvent.Message(
                        "업로드 중입니다.",
                        null
                    )

                    UploadFeedBack.Success -> SnackBarEvent.Message(
                        "업로드 완료되었습니다.",
                        "갤러리에서 확인하기"
                    ) {
                        viewModelScope.launch {
                            _navigationRequest.emit(SnackBarNavigation.ToGallery)
                        }
                    }

                    is UploadFeedBack.Error -> SnackBarEvent.Message(
                        "업로드에 실패했습니다.",
                        "업로드로 이동하기"
                    ) {
                        viewModelScope.launch {
                            _navigationRequest.emit(SnackBarNavigation.ToUpload(feedback.uploadModel))
                        }
                    }
                }
                _snackBarFlow.emit(event)
            }
        }
    }

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
