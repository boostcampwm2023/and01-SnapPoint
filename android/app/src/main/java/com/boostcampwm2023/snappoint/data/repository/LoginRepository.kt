package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.model.request.SignupRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.LoginResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignupResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun postLogin(email: String, password: String): Flow<LoginResponse>

    fun getLogout(): Flow<Unit>

    fun postSignup(email: String, password: String, nickname: String): Flow<SignupResponse>
}