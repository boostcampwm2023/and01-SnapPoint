package com.boostcampwm2023.snappoint.presentation.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class PostSummaryState(
    val uuid: String = "",
    val title: String = "",
    val author: String = "",
    val timeStamp: String = "",
    val summary: String = "",
    val postBlocks: List<PostBlockState> = emptyList()
)

sealed class PostBlockState(open val content: String, open val isEditMode: Boolean, open val uuid: String) {
    data class TEXT(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
    ) : PostBlockState(content, isEditMode, uuid)
    data class IMAGE(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
        val address: String = "",
        val bitmap: Bitmap? = null
    ) : PostBlockState(content, isEditMode, uuid)
    data class VIDEO(
        override val content: String = "",
        override val isEditMode: Boolean = false,
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
        val address: String = ""
    ) : PostBlockState(content, isEditMode, uuid)

    enum class ViewType {
        TEXT,
        IMAGE,
        VIDEO,
    }
}

data class PositionState(
    val latitude: Double,
    val longitude: Double
){
    fun asDoubleArray(): DoubleArray{
        return doubleArrayOf(latitude, longitude)
    }
    fun asLatLng(): LatLng{
        return LatLng(latitude, longitude)
    }
}
