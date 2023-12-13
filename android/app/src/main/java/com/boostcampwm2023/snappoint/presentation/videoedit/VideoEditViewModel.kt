package com.boostcampwm2023.snappoint.presentation.videoedit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val _recentState: MutableStateFlow<Long> = MutableStateFlow(0L)
    val recentState: StateFlow<Long> = _recentState.asStateFlow()
    private val _TLVWidth: MutableStateFlow<Float> = MutableStateFlow(0F)
    val TLVWidth: StateFlow<Float> = _TLVWidth.asStateFlow()
    private val _TLVHeight: MutableStateFlow<Float> = MutableStateFlow(0F)
    val TLVHeight: StateFlow<Float> = _TLVHeight.asStateFlow()
    private val _videoLengthInMs: MutableStateFlow<Float> = MutableStateFlow(0F)
    val videoLengthInMs: StateFlow<Float> = _videoLengthInMs.asStateFlow()
    private val _isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _event: MutableSharedFlow<VideoEditEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<VideoEditEvent> = _event.asSharedFlow()

    fun onLeftThumbMoved(secDivideTen: Long){
        _leftThumbState.update {
            secDivideTen
        }
        _event.tryEmit(VideoEditEvent.StopPlayer)
        updateRecent(_leftThumbState.value)
    }
    fun onRightThumbMoved(secDivideTen: Long){
        _rightThumbState.update {
            secDivideTen
        }
        _event.tryEmit(VideoEditEvent.StopPlayer)
        updateRecent(_rightThumbState.value)
    }

    fun setUri(uri: String) {
        _uri.update {
            uri
        }
    }

    fun startLoading(){
        _isLoading.update {
            true
        }
    }
    fun finishLoading(){
        _isLoading.update {
            false
        }
    }

    fun updateRecent(time: Long) {
        _recentState.update {
            time
        }
    }

    fun updateTLVSize(width: Int, height: Int) {
        _TLVWidth.update {
            width.toFloat()
        }
        _TLVHeight.update {
            height.toFloat()
        }
    }

    fun updateVideoLengthWithRightThumb(videoLengthInMs: Long) {
        _rightThumbState.value = videoLengthInMs
        _videoLengthInMs.update {
            videoLengthInMs.toFloat()
        }
    }

    fun onPlayButtonClicked() {
        _event.tryEmit(VideoEditEvent.OnPlayButtonClicked)
    }

    fun playerIsPlaying(isPlaying: Boolean) {
        _isPlaying.update {
            isPlaying
        }
    }

    fun onBackButtonClicked() {
        _event.tryEmit(VideoEditEvent.OnBackButtonClicked)
    }

    fun onUploadWithoutEditButtonClicked() {
        _event.tryEmit(VideoEditEvent.OnUploadWithoutEditButtonClicked)
    }
    fun onCheckButtonClicked() {
        _event.tryEmit(VideoEditEvent.OnCheckButtonClicked)
    }

}