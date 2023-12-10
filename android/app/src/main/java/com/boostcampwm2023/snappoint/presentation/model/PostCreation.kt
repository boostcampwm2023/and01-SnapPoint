package com.boostcampwm2023.snappoint.presentation.model

import android.graphics.Bitmap
import android.net.Uri

data class PostCreationState(
    val uuid: String = "",
    val title: String = "",
    val author: String = "",
    val timeStamp: String = "",
    val summary: String = "",
    val postBlocks: List<PostBlockCreationState> = emptyList()
)

sealed class PostBlockCreationState {
    abstract val content: String
    abstract val isEditMode: Boolean
    abstract val uuid: String

    data class TEXT(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
    ) : PostBlockCreationState()

    data class IMAGE(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
        val address: String = "",
        val bitmap: Bitmap? = null,
        val fileUuid: String = ""
    ) : PostBlockCreationState()

    data class VIDEO(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
        val address: String = "",
        val mimeType: String = "",
        val uri:Uri,
        val resultPath: String = "",
        val fileUuid: String = ""
    ) : PostBlockCreationState()

    enum class ViewType {
        TEXT,
        IMAGE,
        VIDEO,
    }
}