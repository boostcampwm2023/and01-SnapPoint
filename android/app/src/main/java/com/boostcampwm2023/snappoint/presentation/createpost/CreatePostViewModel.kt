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
        onTextChanged = { index, content ->
            updatePostBlocks(index, content)
        },
        onDeleteButtonClicked = { index ->
            deletePostBlock(index)
        },
        onAddressIconClicked = { index ->
            findAddress(index)
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

    private fun deletePostBlock(position: Int) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.filterIndexed { index, _ ->
                    index != position
                }
            )
        }
    }

    private fun updatePostBlocks(index: Int, content: String) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.mapIndexed { idx, postBlock ->
                    if(index == idx) {
                        when(postBlock){
                            is PostBlockState.STRING -> postBlock.copy(content = content)
                            is PostBlockState.IMAGE -> postBlock.copy(content = content)
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
            return
        }
        postRepository.postCreatePost(
            title = _uiState.value.title,
            postBlocks = _uiState.value.postBlocks
        )
            .onStart {
                _uiState.update {
                    it.copy(isLoading = true)
                }
            }
            .catch { Log.d("TAG", "onCheckButtonClicked: error occurred, ${it.message}") }
            .onCompletion {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
            .onEach {
                Log.d("TAG", "onCheckButtonClicked: api request success")
                _event.tryEmit(CreatePostEvent.NavigatePrev)
            }
            .launchIn(viewModelScope)
    }

    fun onImageBlockButtonClicked() {
        _event.tryEmit(CreatePostEvent.SelectImageFromLocal)
    }

    fun onBackButtonClicked(){
        _event.tryEmit(CreatePostEvent.NavigatePrev)
    }


    private fun findAddress(index: Int) {

        when(val target = _uiState.value.postBlocks[index]){
            is PostBlockState.STRING -> {return}
            is PostBlockState.IMAGE -> {
                _event.tryEmit(CreatePostEvent.FindAddress(index, target.position))
            }
            is PostBlockState.VIDEO -> {
                _event.tryEmit(CreatePostEvent.FindAddress(index, target.position))
            }
        }
    }

    fun setAddressAndPosition(index: Int, address: String, position: PositionState) {

        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.mapIndexed { idx, postBlock ->
                    if(idx == index){
                        when(postBlock){
                            is PostBlockState.IMAGE -> { PostBlockState.IMAGE(postBlock.content, postBlock.uri, position, address)}
                            is PostBlockState.VIDEO -> {PostBlockState.VIDEO(postBlock.content, postBlock.uri, position, address)}
                            is PostBlockState.STRING -> {postBlock}
                        }
                    }else{
                        postBlock
                    }
                }
            )
        }
    }
}