package com.boostcampwm2023.snappoint.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

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
        loginRepository.postLogin(loginFormUiState.value.isEmailValid.toString(), loginFormUiState.value.isPasswordValid.toString())
            .onStart {
                _loginFormUiState.update {
                    it.copy(
                        isLoginInProgress = true
                    )
                }
            }
            .onEach {

            }
            .catch {

            }
            .onCompletion {

            }
            .launchIn(viewModelScope)
    }

    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }
}