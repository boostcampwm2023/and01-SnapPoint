package com.boostcampwm2023.snappoint.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _loginFormUiState: MutableStateFlow<LoginFormState> = MutableStateFlow(LoginFormState())
    val loginFormUiState: StateFlow<LoginFormState> = _loginFormUiState.asStateFlow()

    fun updateEmail(email: String) {
        _loginFormUiState.update {
            it.copy(
                isEmailValid = isEmailValid(email)
            )
        }
    }

    fun updatePassword(password: String) {
        _loginFormUiState.update {
            it.copy(
                isPasswordValid = isPasswordValid(password)
            )
        }
    }

    fun tryLogin() {
        // TODO
        _loginFormUiState.update {
            it.copy(
                isLoginInProgress = true
            )
        }
    }

    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }
}