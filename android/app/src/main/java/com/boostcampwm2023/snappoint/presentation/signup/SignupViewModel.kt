package com.boostcampwm2023.snappoint.presentation.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class SignupViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<SignupUiState> = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun updatePasswordConfirm(password: String) {
        _uiState.update {
            it.copy(passwordConfirm = password)
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.update {
            it.copy(nickname = nickname)
        }
    }
}