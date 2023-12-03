package com.boostcampwm2023.snappoint.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAroundPostRequest(
    @SerialName("lb") val leftBottom: String,
    @SerialName("rt") val rightTop: String,
)
