package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri

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


sealed class PostBlockState(open val content: String, open val isEditMode: Boolean) {
    data class STRING(override val content: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
    data class IMAGE(override val content: String = "", val uri: Uri, val position: PositionState, val address: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
    data class VIDEO(override val content: String = "", val uri: Uri, val position: PositionState, val address: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
}

data class PositionState(
    val latitude: Double,
    val longitude: Double
){
    fun asDoubleArray(): DoubleArray{
        return doubleArrayOf(latitude, longitude)
    }
}
