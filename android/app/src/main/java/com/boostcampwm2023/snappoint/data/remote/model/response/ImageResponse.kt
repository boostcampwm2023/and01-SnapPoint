package com.boostcampwm2023.snappoint.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("image")
    val image: String,
)
