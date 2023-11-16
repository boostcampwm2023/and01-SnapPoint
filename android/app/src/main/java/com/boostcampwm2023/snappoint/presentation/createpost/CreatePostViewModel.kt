package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.boostcampwm2023.snappoint.R
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
class CreatePostViewModel @Inject constructor() : ViewModel() {

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

    fun addImageBlock(uri: Uri?) {
        if (uri == null) return

        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks + PostBlock.IMAGE(uri = uri, position = Position(0.0, 0.0))
            )
        }
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
    }

    fun onImageBlockButtonClicked() {
        _event.tryEmit(CreatePostEvent.SelectImageFromLocal)
    }
}