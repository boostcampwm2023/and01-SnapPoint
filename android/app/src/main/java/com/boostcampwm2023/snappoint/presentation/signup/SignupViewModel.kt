package com.boostcampwm2023.snappoint.presentation.signup

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<SignupUiState> = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailCode = takeEmailErrorCode(email)
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordCode = takePasswordErrorCode(password)
            )
        }
    }

    fun updatePasswordConfirm(password: String) {
        _uiState.update {
            it.copy(
                passwordConfirm = password,
                passwordConfirmCode = takePasswordConfirmErrorCode(password)
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
    }

    private fun takeEmailErrorCode(email: String): Int? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else R.string.app_name
    }

    private fun takePasswordErrorCode(password: String): Int? {
        return if (!Regex("[a-zA-Z]+").matches(password)) {
            Log.d("LOG", "alphabet")
            R.string.app_name
        } else if (!Regex("[0-9]").matches(password)) {
            Log.d("LOG", "number")
            R.string.create_post_fragment_appbar_title
        } else if (!Regex("[!@#$%^&*()\\-_]").matches(password)) {
            Log.d("LOG", "special char")
            R.string.menu_around
        } else {
            null
        }
    }

    private fun takePasswordConfirmErrorCode(password: String): Int? {
        return if (uiState.value.password == password) null else R.string.app_name
    }

    private fun takeNicknameErrorCode(nickname: String): Int? {
        return if (nickname.length > 2) null else R.string.app_name
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