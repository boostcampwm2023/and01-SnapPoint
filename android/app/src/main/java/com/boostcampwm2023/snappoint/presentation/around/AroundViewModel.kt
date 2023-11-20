package com.boostcampwm2023.snappoint.presentation.around

import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.presentation.main.MainUiState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AroundViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState: MutableStateFlow<AroundUiState> = MutableStateFlow(AroundUiState())
    val uiState: StateFlow<AroundUiState> = _uiState.asStateFlow()


    fun updatePosts(posts: List<PostSummaryState>) {
        _uiState.update {
            it.copy(
                posts = posts
            )
        }
    }
}
