package com.boostcampwm2023.snappoint.presentation.viewpost.post

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.main.MainUiState
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<PostUiState> = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

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
}