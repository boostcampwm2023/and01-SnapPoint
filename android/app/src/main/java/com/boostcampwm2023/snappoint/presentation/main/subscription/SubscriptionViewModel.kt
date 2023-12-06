package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.RoomRepository
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import com.boostcampwm2023.snappoint.presentation.util.SignInUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val signInUtil: SignInUtil
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

    private fun previewButtonClicked(index: Int) {
        _event.tryEmit(SubscriptionEvent.ShowSnapPointAndRoute(index))
    }

    private fun viewPostButtonClicked(index: Int) {
        _event.tryEmit(SubscriptionEvent.NavigateViewPost(index))
    }

    fun getSavedPost() {
        roomRepository.getLocalPosts(signInUtil.getEmail())
            .onEach {
                Log.d("LOG", it.toString())
            }.catch {
                Log.d("LOG", "Catch: ${it.message}}")
            }.launchIn(viewModelScope)
    }
}