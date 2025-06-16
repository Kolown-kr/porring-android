package com.kolown.porring.feature.setting.delete_account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DeletedAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var _isDeleteAccount = MutableSharedFlow<Boolean>()
    val isDeleteAccount = _isDeleteAccount.asSharedFlow()

    fun onPasswordChange(password: String) {
        _state.update {
            it.copy(password = password)
        }
    }

    fun deleteAccount() = viewModelScope.launch {
        authRepository
            .deleteAccount(
                state.value.password
            )
            .also {
                Log.d("deleteAccount", it.toString())
            }
            .onSuccess {
                _isDeleteAccount.emit(true)
            }
            .onFailure {
                _isDeleteAccount.emit(false)
            }
    }

    data class State(
        val password: String = "",
    ) {
        val isEnableButton = password.isNotBlank()
    }
}