package com.boostcampwm2023.snappoint.presentation.videoedit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class VideoEditViewModel @Inject constructor(
):ViewModel() {

    private val _uri: MutableStateFlow<String> = MutableStateFlow("")
    val uri: StateFlow<String> = _uri.asStateFlow()

    private val _leftThumbState: MutableStateFlow<Long> = MutableStateFlow(0L)
    val leftThumbState: StateFlow<Long> = _leftThumbState.asStateFlow()
    private val _rightThumbState: MutableStateFlow<Long> = MutableStateFlow(0L)
    val rightThumbState: StateFlow<Long> = _rightThumbState.asStateFlow()

    fun onLeftThumbMoved(position: Long){
        _leftThumbState.update { position }

    }
    fun onRightThumbMoved(position: Long){
        _rightThumbState.update { position }
    }

    fun setUri(uri: String) {
        _uri.update {
            uri
        }
    }
}