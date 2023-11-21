package com.boostcampwm2023.snappoint.presentation.around

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
class AroundViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState: MutableStateFlow<AroundUiState> = MutableStateFlow(AroundUiState(
        onPreviewButtonClicked = { index ->
            previewButtonClicked(index)
        }
    ))
    val uiState: StateFlow<AroundUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<AroundEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<AroundEvent> = _event.asSharedFlow()

    fun updatePosts(posts: List<PostSummaryState>) {
        _uiState.update {
            it.copy(
                posts = posts
            )
        }
    }

    private fun previewButtonClicked(index: Int) {
        _event.tryEmit(AroundEvent.ShowSnapPointAndRoute(index))
    }
}
