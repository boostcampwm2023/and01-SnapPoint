package com.boostcampwm2023.snappoint.presentation.createpost

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        
    }
}