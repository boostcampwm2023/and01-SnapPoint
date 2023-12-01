package com.boostcampwm2023.snappoint.presentation.viewpost

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
import javax.inject.Inject

@HiltViewModel
class ViewPostViewModel @Inject constructor() : ViewModel() {

    private val _selectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _posts: MutableStateFlow<List<PostSummaryState>> = MutableStateFlow(emptyList())
    val posts: StateFlow<List<PostSummaryState>> = _posts.asStateFlow()

    private val _event: MutableSharedFlow<ViewPostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<ViewPostEvent> = _event.asSharedFlow()

    fun updateSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    init {
        loadPosts()
    }

    private fun loadPosts() {

    }

    fun finishPostView() {
        _event.tryEmit(ViewPostEvent.FinishActivity)
    }
}