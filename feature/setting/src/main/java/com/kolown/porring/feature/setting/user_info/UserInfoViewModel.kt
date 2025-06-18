package com.kolown.porring.feature.setting.user_info

import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.ui.base.BaseViewModel
import javax.inject.Inject

internal class UserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
): BaseViewModel<UserInfoViewModel.State>(State()) {

    fun init() = launch {
        userRepository.getUserData()
    }

    data class State(
        val state: String = ""
    )
}
