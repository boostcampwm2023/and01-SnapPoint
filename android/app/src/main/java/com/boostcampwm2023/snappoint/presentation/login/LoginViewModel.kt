package com.boostcampwm2023.snappoint.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _loginState: MutableStateFlow<LoginFormState> = MutableStateFlow(LoginFormState())
    val loginState: StateFlow<LoginFormState> = _loginState.asStateFlow()

    fun updateUsername(username: String) {
        _loginState.update {
            it.copy(
                isUsernameValid = isUsernameValid(username)
            )
        }
    }

    fun updatePassword(password: String) {
        _loginState.update {
            it.copy(
                isPasswordValid = isPasswordValid(password)
            )
        }
    }

    fun tryLogin() {
        // TODO
        _loginState.update {
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