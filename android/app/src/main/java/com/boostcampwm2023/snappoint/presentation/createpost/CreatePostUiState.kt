package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

data class CreatePostUiState(
    val title: String = "",
    val postBlocks: List<PostBlockState> = mutableListOf(),
    val onTextChanged: (index: Int, content: String) -> Unit,
    val onDeleteButtonClicked: (position: Int) -> Unit,
    val onAddressIconClicked: (index: Int) -> Unit,
    val onEditButtonClicked: (position: Int) -> Unit,
    val onCheckButtonClicked: (position: Int) -> Unit,
    val onUpButtonClicked: (position: Int) -> Unit,
    val onDownButtonClicked: (position: Int) -> Unit,
    val isLoading: Boolean = false,
)


