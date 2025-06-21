package com.kolown.porring.feature.setting.user_info

import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.ui.base.BaseViewModel
import java.time.LocalDateTime
import javax.inject.Inject

internal class UserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
): BaseViewModel<UserInfoViewModel.State>(State()) {

    fun init() = launch {
        userRepository.getUserData()
    }

    data class State(
        val userId: String = "",
        val userEmail: String = "",
        val createAt: LocalDateTime = LocalDateTime.now(),
        val totalPhotos: Int = 0,
        val totalFollow: Int = 0,

        val isEmailDialogVisible: Boolean = false,
        val dialogEmailAddress: String = "",

        val isDeleteDialogVisible: Boolean = false,
        val dialogPassword: String = "",
    ) {
    }
}
