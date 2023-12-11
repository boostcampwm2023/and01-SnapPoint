package com.boostcampwm2023.snappoint.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.SignInRepository
import com.boostcampwm2023.snappoint.presentation.util.UserInfoPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userInfoPreference: UserInfoPreference,
    private val loginRepository: SignInRepository
) : ViewModel() {

    private val _event: MutableSharedFlow<SplashEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SplashEvent> = _event.asSharedFlow()

    fun login() {
        val email = userInfoPreference.getEmail()
        val password = userInfoPreference.getPassword()

        loginRepository.postSignIn(email, password)
            .onEach {
                _event.emit(SplashEvent.Success)
            }
            .catch {
                _event.emit(SplashEvent.Fail)
            }
            .launchIn(viewModelScope)
    }
}