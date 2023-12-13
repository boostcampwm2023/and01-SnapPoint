package com.boostcampwm2023.snappoint.presentation.main.subscription

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
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
class SubscriptionViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SubscriptionUiState> = MutableStateFlow(
        SubscriptionUiState(
            onPreviewButtonClicked = { index -> previewButtonClicked(index) },
            onViewPostButtonClicked = { index -> viewPostButtonClicked(index) }
        )
    )
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<SubscriptionEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SubscriptionEvent> = _event.asSharedFlow()

    fun updatePosts(posts: List<PostSummaryState>) {
        _uiState.update {
            it.copy(
                posts = posts
            )
        }
    }

    fun setViewPostState(isOpened: Boolean) {
        _uiState.update {
            it.copy(
                isViewPostOpened = isOpened
            )
        }
    }

    private fun previewButtonClicked(index: Int) {
        _event.tryEmit(SubscriptionEvent.ShowSnapPointAndRoute(index))
    }

    private fun viewPostButtonClicked(index: Int) {
        _event.tryEmit(SubscriptionEvent.NavigateViewPost(index))
    }
}