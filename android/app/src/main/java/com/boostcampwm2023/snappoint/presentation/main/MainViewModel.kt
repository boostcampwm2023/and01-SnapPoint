package com.boostcampwm2023.snappoint.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor()
    :ViewModel(){

        private val _event: MutableSharedFlow<MainActivityEvent> = MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val event: SharedFlow<MainActivityEvent> = _event.asSharedFlow()

        fun drawerIconClicked() {
            _event.tryEmit(MainActivityEvent.OpenDrawer)
        }
}