package com.boostcampwm2023.snappoint.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignupResponse(
    @SerialName("id") val id: Int,
    @SerialName("uuid") val uuid: String,
    @SerialName("email") val email: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("modifiedAt") val modifiedAt: String,
    @SerialName("isDeleted") val isDeleted: Boolean
)