package com.boostcampwm2023.snappoint.presentation.viewpost.post

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
class PostViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<PostUiState> = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<PostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<PostEvent> = _event.asSharedFlow()

    fun updatePost(post: PostSummaryState) {
        _uiState.update {
            PostUiState(
                title = post.title,
                author = post.author,
                timestamp = post.timeStamp,
                posts = post.postBlocks,
            )
        }
    }

    fun navigateToPrevious() {
        _event.tryEmit(PostEvent.NavigatePrev)
    }
}