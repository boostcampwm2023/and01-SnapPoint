package com.boostcampwm2023.snappoint.presentation.createpost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.PostRepository
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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<CreatePostUiState> = MutableStateFlow(CreatePostUiState(
        onTextChanged = { position, content ->
            updatePostBlocks(position, content)
        }
    ))
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<CreatePostEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<CreatePostEvent> = _event.asSharedFlow()


    fun addTextBlock() {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.plus(PostBlock.STRING())
            )
        }
    }

    fun addImageBlock() {
        TODO()
    }

    fun addVideoBlock() {
        TODO()
    }

    private fun updatePostBlocks(position: Int, content: String) {
        _uiState.update {
            it.copy(
//                postBlocks = it.postBlocks.toMutableList().apply {
//                    when (val postBlock = this[position]) {
//                        is PostBlock.STRING -> set(position, postBlock.copy(content = content))
//                        is PostBlock.IMAGE -> TODO()
//                        is PostBlock.VIDEO -> TODO()
//                    }
//                }
                postBlocks = it.postBlocks.mapIndexed { index, postBlock ->
                    if(position == index) {
                        when(postBlock){
                            is PostBlock.STRING -> postBlock.copy(content = content)
                            is PostBlock.IMAGE -> TODO()
                            is PostBlock.VIDEO -> TODO()
                        }
                    }else{
                        postBlock
                    }
                }
            )
        }
        Log.d("TAG", "updatePostBlocks: ${_uiState.value.postBlocks[position].content}")
    }

    private fun isValidContents(): Boolean {
        _uiState.value.postBlocks.forEach {
            when(it){
                is PostBlock.STRING -> {if(it.content.isEmpty()) return false}
                is PostBlock.IMAGE -> {if(it.content.isEmpty()) return false}
                is PostBlock.VIDEO -> {if(it.content.isEmpty()) return false}
            }
        }
        return true
    }

    fun onCheckButtonClicked() {
        if(isValidContents().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_block))
        }
        postRepository.postPost(_uiState.value.postBlocks)
            .onStart {  }
            .catch {  }
            .onCompletion {  }
            .onEach {
                _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_post_api_success))
            }
            .launchIn(viewModelScope)
    }
}