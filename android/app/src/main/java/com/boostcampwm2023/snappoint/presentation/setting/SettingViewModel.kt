package com.boostcampwm2023.snappoint.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.SignInRepository
import com.boostcampwm2023.snappoint.presentation.util.SignInUtil
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
class SettingViewModel @Inject constructor(
    private val signInUtil: SignInUtil,
    private val loginRepository: SignInRepository
) : ViewModel() {

    private val _event: MutableSharedFlow<SettingEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SettingEvent> = _event.asSharedFlow()

    fun onSignOutClick() {
        loginRepository.getSignOut()
            .onEach {
                signInUtil.clearUserAuthData()
                _event.emit(SettingEvent.SignOut)
            }
            .catch {
                _event.emit(SettingEvent.FailToSignOut)
            }
            .launchIn(viewModelScope)
    }

    fun onClearSnapPointClick() {
        _event.tryEmit(SettingEvent.RemoveSnapPoint)
    }
}