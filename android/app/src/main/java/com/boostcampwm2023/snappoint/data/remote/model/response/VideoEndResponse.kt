package com.boostcampwm2023.snappoint.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoEndResponse(
    @SerialName("presignedUrl")
    val preSignedUrl:String,
)
