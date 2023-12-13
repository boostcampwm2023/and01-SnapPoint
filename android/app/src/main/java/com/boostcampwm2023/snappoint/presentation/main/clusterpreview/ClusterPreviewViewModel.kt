package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
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
class ClusterPreviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<ClusterPreviewUiState> = MutableStateFlow(ClusterPreviewUiState(
        onItemClicked = { clusterItemClicked(it) }
    ))
    val uiState: StateFlow<ClusterPreviewUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<ClusterPreviewEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<ClusterPreviewEvent> = _event.asSharedFlow()

    fun updatePostList(posts: List<PostBlockState>) {
        _uiState.update {
            it.copy(clusters = posts)
        }
    }

    private fun clusterItemClicked(index: Int) {
        _event.tryEmit(ClusterPreviewEvent.NavigateClusterImage(index))
    }
}