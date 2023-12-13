package com.boostcampwm2023.snappoint.presentation.main.preview

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
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
class PreviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<PreviewUiState> = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<PreviewEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<PreviewEvent> = _event.asSharedFlow()

    fun updatePost(post: PostSummaryState) {
        _uiState.update {
            it.copy(
                uuid = post.uuid,
                title = post.title,
                timeStamp = post.timeStamp,
                blocks = post.postBlocks
            )
        }
    }

    fun onArrowButtonClick() {
        _event.tryEmit(PreviewEvent.NavigateViewPost)
    }
}