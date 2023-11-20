package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.repository.PostRepository
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
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
        },
        onEditButtonClicked = { position ->
            changeToEditMode(position)
        },
        onCheckButtonClicked = { position ->
            clearEditMode(position)
        },
        onUpButtonClicked = { position ->
            moveUp(position)
        },
        onDownButtonClicked = { position ->
            moveDown(position)
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

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(title = title)
        }
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

    private fun changeToEditMode(position: Int) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.mapIndexed { index, postBlock ->
                    if (position == index) {
                        when (postBlock) {
                            is PostBlockState.STRING -> postBlock.copy(isEditMode = true)
                            is PostBlockState.IMAGE -> postBlock.copy(isEditMode = true)
                            is PostBlockState.VIDEO -> postBlock.copy(isEditMode = true)
                        }
                    } else {
                        when (postBlock) {
                            is PostBlockState.STRING -> postBlock.copy(isEditMode = false)
                            is PostBlockState.IMAGE -> postBlock.copy(isEditMode = false)
                            is PostBlockState.VIDEO -> postBlock.copy(isEditMode = false)
                        }
                    }
                }
            )
        }
    }

    private fun clearEditMode(position: Int) {
        _uiState.update {
            it.copy(
                postBlocks = it.postBlocks.mapIndexed { index, postBlock ->
                    if (position == index) {
                        when (postBlock) {
                            is PostBlockState.STRING -> postBlock.copy(isEditMode = false)
                            is PostBlockState.IMAGE -> postBlock.copy(isEditMode = false)
                            is PostBlockState.VIDEO -> postBlock.copy(isEditMode = false)
                        }
                    } else {
                        postBlock
                    }
                }
            )
        }
    }

    private fun moveUp(position: Int) {
        if (position == 0) return
        _uiState.update {
            val list = it.postBlocks.toMutableList()
            val tmp = list[position]
            list[position] = list[position - 1]
            list[position - 1] = tmp
            it.copy(postBlocks = list)
        }
    }

    private fun moveDown(position: Int) {
        if (position == uiState.value.postBlocks.lastIndex) return
        _uiState.update {
            val list = it.postBlocks.toMutableList()
            val tmp = list[position]
            list[position] = list[position + 1]
            list[position + 1] = tmp
            it.copy(postBlocks = list)
        }
    }

    private fun isValidTitle(): Boolean {
        return _uiState.value.title.isNotBlank()
    }

    private fun isValidBlocks(): Boolean {
        return _uiState.value.postBlocks.isNotEmpty()
    }

    private fun isValidTextBlock(): Boolean {
        return _uiState.value.postBlocks.find { it is PostBlockState.STRING && it.content.isEmpty() } == null
    }

    private fun isValidMediaBlock(): Boolean {
        return _uiState.value.postBlocks.find { (it is PostBlockState.STRING).not() } != null
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
        if(isValidTitle().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_title))
            return
        }
        if(isValidBlocks().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_blocks))
            return
        }
        if(isValidTextBlock().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_text))
            return
        }
        if(isValidMediaBlock().not()){
            _event.tryEmit(CreatePostEvent.ShowMessage(R.string.create_post_fragment_empty_media))
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
                            is PostBlockState.IMAGE ->  PostBlockState.IMAGE(content = postBlock.content, uri = postBlock.uri, position = position, address = address)
                            is PostBlockState.VIDEO -> PostBlockState.VIDEO(content = postBlock.content, uri = postBlock.uri, position = position, address = address)
                            is PostBlockState.STRING -> postBlock
                        }
                    }else{
                        postBlock
                    }
                }
            )
        }
    }
}