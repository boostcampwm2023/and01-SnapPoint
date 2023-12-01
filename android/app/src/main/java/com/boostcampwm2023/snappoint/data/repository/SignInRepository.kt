package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.model.response.SignInResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignupResponse
import kotlinx.coroutines.flow.Flow

interface SignInRepository {

    fun postSignIn(email: String, password: String): Flow<SignInResponse>

    fun getSignOut(): Flow<Unit>

    fun postSignUp(email: String, password: String, nickname: String): Flow<SignupResponse>
}