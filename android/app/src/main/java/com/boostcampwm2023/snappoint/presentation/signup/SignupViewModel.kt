package com.boostcampwm2023.snappoint.presentation.signup

import android.util.Log
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

    fun trySignup() {
        with(uiState.value) {
            val email = email
            val password = password
            val passwordConfirm = passwordConfirm
            val nickname = nickname

            if (password != passwordConfirm) {
                Log.d("LOG", "mismatch")
            } else if (!Regex("[a-zA-Z]+").matches(password)) {
                Log.d("LOG", "alphabet")
            } else if (!Regex("[0-9]").matches(password)) {
                Log.d("LOG", "number")
            } else if (!Regex("[!@#$%^&*()\\-_]").matches(password)) {
                Log.d("LOG", "special char")
            } else {

            }
        }
    }
}