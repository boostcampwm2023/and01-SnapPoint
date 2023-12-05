package com.boostcampwm2023.snappoint.presentation.viewpost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ViewPostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _post: MutableStateFlow<PostSummaryState> = MutableStateFlow(PostSummaryState())
    val post: StateFlow<PostSummaryState> = _post.asStateFlow()

    private val _event: MutableSharedFlow<ViewPostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<ViewPostEvent> = _event.asSharedFlow()

    fun loadPost(uuid: String) {
        postRepository.getPost(uuid)
            .onStart {
                _post.update {
                    it.copy(uuid = uuid)
                }
            }
            .catch {
                Log.d("TAG", "loadPost: ${it.message}")
            }
            .onEach { response ->
                _post.update {
                    response
                }
            }
            .launchIn(viewModelScope)
    }

    fun finishPostView() {
        _event.tryEmit(ViewPostEvent.FinishActivity)
    }
}