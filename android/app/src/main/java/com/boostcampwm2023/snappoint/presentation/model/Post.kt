package com.boostcampwm2023.snappoint.presentation.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class PostSummaryState(
    val uuid: String = "",
    val title: String = "",
    val author: String = "",
    val timeStamp: String = "",
    val summary: String = "",
    val postBlocks: List<PostBlockState> = emptyList()
)

@Serializable
sealed class PostBlockState {
    abstract val content: String
    abstract val uuid: String
    @Serializable
    data class TEXT(
        override val content: String = "",
        override val uuid: String = "",
    ) : PostBlockState()
    @Serializable
    data class IMAGE(
        override val content: String = "",
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
    ) : PostBlockState()
    @Serializable
    data class VIDEO(
        override val content: String = "",
        override val uuid: String = "",
        val description: String = "",
        val position: PositionState = PositionState(0.0, 0.0),
    ) : PostBlockState()
    @Serializable
    enum class ViewType {
        TEXT,
        IMAGE,
        VIDEO,
    }
}

@Serializable
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
