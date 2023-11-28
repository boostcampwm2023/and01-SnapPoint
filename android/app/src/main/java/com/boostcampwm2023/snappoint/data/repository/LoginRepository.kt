package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.model.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun postLogin(email: String, password: String): Flow<LoginResponse>
}