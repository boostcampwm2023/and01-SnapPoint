package com.boostcampwm2023.snappoint.presentation.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class PostSummaryState(
    val title: String,
    val author: String,
    val timeStamp: String,
    val postBlocks: List<PostBlockState>
)

sealed class PostBlockState(open val content: String, open val isEditMode: Boolean) {
    data class STRING(override val content: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
    data class IMAGE(override val content: String = "", val uri: Uri = Uri.EMPTY, val description: String = "", val position: PositionState = PositionState(0.0, 0.0), val address: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
    data class VIDEO(override val content: String = "", val uri: Uri = Uri.EMPTY, val description: String = "", val position: PositionState = PositionState(0.0, 0.0), val address: String = "", override val isEditMode: Boolean = false) : PostBlockState(content, isEditMode)
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
