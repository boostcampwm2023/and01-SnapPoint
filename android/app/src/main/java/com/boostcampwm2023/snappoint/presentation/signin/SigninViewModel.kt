package com.boostcampwm2023.snappoint.presentation.signin

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginFormUiState: MutableStateFlow<SigninFormState> = MutableStateFlow(SigninFormState(
        email = "string@string.com",
        password = "Str!n8Str!n8",
        isEmailValid = true,
        isPasswordValid = true
    ))
    val loginFormUiState: StateFlow<SigninFormState> = _loginFormUiState.asStateFlow()

    private val _event: MutableSharedFlow<SigninEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SigninEvent> = _event.asSharedFlow()

    fun updateEmail(email: String) {
        _loginFormUiState.update {
            it.copy(
                email = email,
                isEmailValid = isEmailValid(email)
            )
        }
    }

    fun updatePassword(password: String) {
        _loginFormUiState.update {
            it.copy(
                password = password,
                isPasswordValid = isPasswordValid(password)
            )
        }
    }

    fun onLoginButtonClick() {
        val email = loginFormUiState.value.email
        // TODO 암호화
        val password = loginFormUiState.value.password

        loginRepository.postLogin(email, password)
            .onStart {
                setProgressBarState(true)
            }
            .onEach {
                _event.emit(SigninEvent.Success)
            }
            .catch {
                _event.emit(SigninEvent.Fail(R.string.login_activity_fail))
            }
            .onCompletion {
                setProgressBarState(false)
            }
            .launchIn(viewModelScope)
    }

    fun onSignUpButtonClick() {
        _event.tryEmit(SigninEvent.Signup)
    }

    private fun setProgressBarState(isInProgress: Boolean) {
        _loginFormUiState.update {
            it.copy(
                isLoginInProgress = isInProgress
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