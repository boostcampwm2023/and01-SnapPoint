package com.boostcampwm2023.snappoint.presentation.createpost

import android.util.Log
import android.net.Uri
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
                postBlocks = it.postBlocks.plus(PostBlockState.STRING())
            )
        }
    }

    fun addImageBlock(uri: Uri?, position: PositionState) {
        if (uri == null) return

        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks + PostBlockState.IMAGE(uri = uri, position = position)
            )
        }
    }

    fun addVideoBlock() {
        TODO()
    }

    private fun updatePostBlocks(position: Int, content: String) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.mapIndexed { index, postBlock ->
                    if(position == index) {
                        when(postBlock){
                            is PostBlockState.STRING -> postBlock.copy(content = content)
                            is PostBlockState.IMAGE -> TODO()
                            is PostBlockState.VIDEO -> TODO()
                        }
                    }else{
                        postBlock
                    }
                }
            )
        }
    }

    private fun isValidContents(): Boolean {
        _uiState.value.postBlocks.forEach {
            when(it){
                is PostBlockState.STRING -> {if(it.content.isEmpty()) return false}
                is PostBlockState.IMAGE -> {if(it.content.isEmpty()) return false}
                is PostBlockState.VIDEO -> {if(it.content.isEmpty()) return false}
            }
        }
        return true
    }

    fun onCheckButtonClicked() {
        println(uiState.value.postBlocks)
        if(isValidContents().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_block))
        }
        postRepository.postCreatePost(
            title = _uiState.value.title,
            postBlocks = _uiState.value.postBlocks
        )
            .onStart { Log.d("TAG", "onCheckButtonClicked: started, loading") }
            .catch { Log.d("TAG", "onCheckButtonClicked: error occurred, ${it.message}") }
            .onCompletion { Log.d("TAG", "onCheckButtonClicked: finished, end") }
            .onEach {
                Log.d("TAG", "onCheckButtonClicked: api request success")
            }
            .launchIn(viewModelScope)
    }

    fun onImageBlockButtonClicked() {
        _event.tryEmit(CreatePostEvent.SelectImageFromLocal)
    }
}