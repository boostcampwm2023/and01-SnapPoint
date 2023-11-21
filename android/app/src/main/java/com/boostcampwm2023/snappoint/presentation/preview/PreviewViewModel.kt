package com.boostcampwm2023.snappoint.presentation.preview

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<PreviewUiState> = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()


    fun updatePost(post: PostSummaryState) {
        _uiState.update {
            it.copy(
                title = post.title,
                timeStamp = post.timeStamp,
                blocks = post.postBlocks
            )
        }
    }
}