package com.boostcampwm2023.snappoint.presentation.auth

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.login.LoginEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _event: MutableSharedFlow<AuthEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<AuthEvent> = _event.asSharedFlow()

    var idCache: String = ""

    fun sendSuccessResult() {
        _event.tryEmit(AuthEvent.Success)
    }
}