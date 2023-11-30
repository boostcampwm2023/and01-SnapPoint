package com.boostcampwm2023.snappoint.presentation.signup

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.LoginRepository
import com.boostcampwm2023.snappoint.presentation.util.TextVerificationUtil
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
class SignupViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignupUiState> = MutableStateFlow(
        SignupUiState(
            email = "email@email.com",
            password = "asdASD123!@#",
            passwordConfirm = "asdASD123!@#",
            nickname = "nickname",
            isInputValid = true
        )
    )
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailCode = takeEmailErrorCode(email)
            )
        }
        updateButtonState()
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordCode = takePasswordErrorCode(password)
            )
        }
        updatePasswordConfirm()
        updateButtonState()
    }

    fun updatePasswordConfirm(password: String) {
        _uiState.update {
            it.copy(
                passwordConfirm = password,
                passwordConfirmCode = takePasswordConfirmErrorCode(password)
            )
        }
        updateButtonState()
    }

    private fun updatePasswordConfirm() {
        _uiState.update {
            it.copy(
                passwordConfirmCode = takePasswordConfirmErrorCode(it.passwordConfirm)
            )
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.update {
            it.copy(
                nickname = nickname,
                nicknameCode = takeNicknameErrorCode(nickname)
            )
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        _uiState.update {
            it.copy(
                isInputValid = it.emailCode == null &&
                        it.passwordCode == null &&
                        it.passwordConfirmCode == null &&
                        it.nicknameCode == null
            )
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
            if (isInputValid.not()) {
                return
            }

            loginRepository.postSignup(email, password, nickname)
                .onStart {
                    setProgressBarState(true)
                }
                .onEach {

                }
                .catch {

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
}