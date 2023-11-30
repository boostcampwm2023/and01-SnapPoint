package com.boostcampwm2023.snappoint.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.LoginRepository
import com.boostcampwm2023.snappoint.presentation.util.LoginUtil
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
    private val loginUtil: LoginUtil,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _event: MutableSharedFlow<SplashEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SplashEvent> = _event.asSharedFlow()

    fun login() {
        val email = loginUtil.email
        val password = loginUtil.password

        if (email.isBlank() || password.isBlank()) {
            _event.tryEmit(SplashEvent.Fail)
            return
        }

        loginRepository.postLogin(email, password)
            .onEach {
                _event.emit(SplashEvent.Success)
            }
            .catch {
                _event.emit(SplashEvent.Fail)
            }
            .launchIn(viewModelScope)
    }
}