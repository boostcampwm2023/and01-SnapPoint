package com.boostcampwm2023.snappoint.presentation.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.LoginRepository
import com.boostcampwm2023.snappoint.presentation.util.TextVerificationUtil
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
class SignupViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignupUiState> = MutableStateFlow(
        SignupUiState(
            email = "email@email.com",
            password = "asdASD123!@#",
            passwordConfirm = "asdASD123!@#",
            nickname = "nickname",
            isButtonEnabled = true
        )
    )
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<SignupEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SignupEvent> = _event.asSharedFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailErrorResId = takeEmailErrorCode(email)
            )
        }
        updateButtonState()
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordErrorResId = takePasswordErrorCode(password)
            )
        }
        updatePasswordConfirm()
        updateButtonState()
    }

    fun updatePasswordConfirm(password: String) {
        _uiState.update {
            it.copy(
                passwordConfirm = password,
                passwordConfirmErrorResId = takePasswordConfirmErrorCode(password)
            )
        }
        updateButtonState()
    }

    private fun updatePasswordConfirm() {
        _uiState.update {
            it.copy(
                passwordConfirmErrorResId = takePasswordConfirmErrorCode(it.passwordConfirm)
            )
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.update {
            it.copy(
                nickname = nickname,
                nicknameErrorResId = takeNicknameErrorCode(nickname)
            )
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        _uiState.update {
            it.copy(
                isButtonEnabled = it.emailErrorResId == null &&
                        it.passwordErrorResId == null &&
                        it.passwordConfirmErrorResId == null &&
                        it.nicknameErrorResId == null
            )
        }
    }

    private fun updateEmailErrorResId(message: String?) {
        if (isMessageDuplicationError(message)) {
            _uiState.update {
                it.copy(
                    emailErrorResId = R.string.signup_fragment_error_email_duplicate,
                    isButtonEnabled = false
                )
            }
        }
    }

    private fun takeEmailErrorCode(email: String): Int? {
        return if (TextVerificationUtil.isEmailValid(email)) null else R.string.signup_fragment_error_email_form
    }

    private fun takePasswordErrorCode(password: String): Int? {
        return if (TextVerificationUtil.isPasswordValid(password)) null else R.string.signup_fragment_error_password_length
    }

    private fun takePasswordConfirmErrorCode(password: String): Int? {
        return if (uiState.value.password == password) null else R.string.signup_fragment_error_password_confirm_mismatch
    }

    private fun takeNicknameErrorCode(nickname: String): Int? {
        return if (nickname.length > 1) null else R.string.signup_fragment_error_nickname
    }

    fun onSignUpButtonClicked() {
        with(uiState.value) {
            if (isButtonEnabled.not()) {
                return
            }

            loginRepository.postSignup(email, password, nickname)
                .onStart {
                    setProgressBarState(true)
                }
                .onEach {
                    _event.emit(SignupEvent.Success)
                }
                .catch {
                    updateEmailErrorResId(it.message)
                    _event.emit(
                        SignupEvent.Fail(_uiState.value.emailErrorResId ?: R.string.signup_fragment_fail)
                    )
                }
                .onCompletion {
                    setProgressBarState(false)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun setProgressBarState(isInProgress: Boolean) {
        _uiState.update {
            it.copy(
                isSignUpInProgress = isInProgress
            )
        }
    }

    private fun takeErrorMessage(message: String?): Int? {
        return when {
            isMessageDuplicationError(message) -> R.string.signup_fragment_fail_duplicate
            else -> null
        }
    }

    private fun isMessageDuplicationError(message: String?): Boolean {
        return message == "HTTP 409 Conflict"
    }
}