package com.boostcampwm2023.snappoint.presentation.createpost

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<CreatePostUiState> = MutableStateFlow(CreatePostUiState { i: Int, s: String ->
        updatePostBlocks(i, s)
    })
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun addTextBlock() {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.toMutableList().apply { add(PostBlock.STRING()) }
            )
        }
    }

    fun addImageBlock() { TODO() }

    fun addVideoBlock() { TODO() }

    private fun updatePostBlocks(position: Int, value: String) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.toMutableList().apply {
                    when (val postBlock = this[position]) {
                        is PostBlock.STRING -> set(position, postBlock.copy(content = value))
                        is PostBlock.IMAGE -> TODO()
                        is PostBlock.VIDEO -> TODO()
                    }
                }
            )
        }
    }

    fun printBlockList() {
        println(uiState.value.postBlocks)
    }
}