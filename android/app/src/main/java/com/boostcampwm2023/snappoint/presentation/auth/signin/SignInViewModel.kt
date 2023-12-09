package com.boostcampwm2023.snappoint.presentation.auth.signin

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.SignInRepository
import com.boostcampwm2023.snappoint.presentation.util.UserInfoPreference
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
class SignInViewModel @Inject constructor(
    private val userInfoPreference: UserInfoPreference,
    private val loginRepository: SignInRepository
) : ViewModel() {

    private val _signInFormUiState: MutableStateFlow<SignInFormState> = MutableStateFlow(
        SignInFormState(
        email = "string@string.com",
        password = "asdASD123!@#",
        isEmailValid = true,
        isPasswordValid = true
    )
    )
    val signInFormUiState: StateFlow<SignInFormState> = _signInFormUiState.asStateFlow()

    private val _event: MutableSharedFlow<SignInEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SignInEvent> = _event.asSharedFlow()

    fun updateEmail(email: String) {
        _signInFormUiState.update {
            it.copy(
                email = email,
                isEmailValid = isEmailValid(email)
            )
        }
    }

    fun updatePassword(password: String) {
        _signInFormUiState.update {
            it.copy(
                password = password,
                isPasswordValid = isPasswordValid(password)
            )
        }
    }

    fun onLoginButtonClick() {
        val email = signInFormUiState.value.email
        // TODO 암호화
        val password = signInFormUiState.value.password

        loginRepository.postSignIn(email, password)
            .onStart {
                setProgressBarState(true)
            }
            .onEach {
                userInfoPreference.setUserAuthData(email, password)
                _event.emit(SignInEvent.NavigateToMainActivity)
            }
            .catch {
                _event.emit(SignInEvent.ShowMessage(R.string.login_activity_fail))
            }
            .onCompletion {
                setProgressBarState(false)
            }
            .launchIn(viewModelScope)
    }

    fun onSignUpButtonClick() {
        _event.tryEmit(SignInEvent.NavigateToSignup)
    }

    private fun setProgressBarState(isInProgress: Boolean) {
        _signInFormUiState.update {
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