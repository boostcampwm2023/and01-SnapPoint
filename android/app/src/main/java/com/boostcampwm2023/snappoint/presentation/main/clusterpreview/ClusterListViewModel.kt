package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ClusterListViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<ClusterListUiState> = MutableStateFlow(ClusterListUiState())
    val uiState: StateFlow<ClusterListUiState> = _uiState.asStateFlow()

    fun updatePostList(posts: List<PostBlockState>) {
        _uiState.update {
            it.copy(clusters = posts)
        }
    }
}