package com.kolown.porring.feature.setting.user_info

import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.model.User
import com.kolown.porring.core.ui.base.BaseViewModel
import com.kolown.porring.core.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
internal class UserInfoViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val userRepository: UserRepository,
) : BaseViewModel<UserInfoViewModel.State>(State()) {

    private val _showToast = MutableSharedFlow<String>()
    val showToast = _showToast.asSharedFlow()

    fun init() = launch {
        userRepository.getCurrentUser()
            .onSuccess {
                updateState { copy(user = it) }
            }
    }

    fun onEmailChangeClick() = updateState {
        copy(isEmailDialogVisible = true)
    }

    fun onEmailChange(email: String) = updateState {
        val isValid =
            validateEmailUseCase(email)

        copy(
            dialogEmailAddress = email,
            emailValid = isValid,
            emailGuid = if (isValid) null else "이메일 형식이 올바르지 않습니다."
        )
    }

    fun onEmailChangeDismiss() = updateState {
        copy(isEmailDialogVisible = false)
    }

    fun requestEmailChange() = launch {
        userRepository.changeReceiverEmailEmail(state.value.dialogEmailAddress)
            .onSuccess {
                updateState {
                    copy(
                        isEmailDialogVisible = false,
                        user = user.copy(receiverEmail = state.value.dialogEmailAddress)
                    )
                }
            }
    }

    fun onDeleteClick() = updateState {
        copy(isDeleteDialogVisible = true)
    }

    fun onDeletePasswordChange(password: String) = updateState {

        copy(
            dialogPassword = password,
            passwordGuid = null
        )
    }

    fun onDeletePasswordDismiss() = updateState {
        copy(isDeleteDialogVisible = false)
    }

    fun requestDelete() = launch {
        userRepository
            .deleteAccount(
                email = state.value.user.email,
                password = state.value.dialogPassword
            )
            .onSuccess {
                updateState {
                    copy(isDeleteDialogVisible = false)
                }
            }
            .onFailure {
                updateState {
                    copy(passwordGuid = "비밀번호를 확인해주세요.")
                }
            }
    }

    data class State(
        val user: User = User(),

        val isEmailDialogVisible: Boolean = false,
        val dialogEmailAddress: String = "",
        val emailValid: Boolean = false,
        val emailGuid: String? = null,

        val isDeleteDialogVisible: Boolean = false,
        val dialogPassword: String = "",
        val passwordGuid: String? = null,
    ) {
    }
}

//TODO Domain 모듈 생성 후 옮길 것
@Singleton
class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): Boolean =
        Patterns.email.matcher(email).matches()

}