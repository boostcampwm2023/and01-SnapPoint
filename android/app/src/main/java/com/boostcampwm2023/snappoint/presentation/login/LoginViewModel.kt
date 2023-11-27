package com.boostcampwm2023.snappoint.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<LoginFormState> = MutableStateFlow(LoginFormState())
    val uiState: StateFlow<LoginFormState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.update {
            it.copy(
                isUsernameValid = isUsernameValid(username)
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                isPasswordValid = isPasswordValid(password)
            )
        }
    }

    fun tryLogin() {
        // TODO
        _uiState.update {
            it.copy(
                isLoginInProgress = true
            )
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}