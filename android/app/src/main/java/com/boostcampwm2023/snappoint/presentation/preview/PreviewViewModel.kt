package com.boostcampwm2023.snappoint.presentation.preview

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlockState
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

    fun updatePost(title: String, timeStamp: String, list: List<PostBlockState>) {
        _uiState.update{
            it.copy(
                title = title,
                timeStamp = timeStamp,
                blocks = list
            )
        }
    }
}