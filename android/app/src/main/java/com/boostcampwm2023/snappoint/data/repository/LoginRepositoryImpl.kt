package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.request.LoginRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.SignupRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.LoginResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val snapPointApi: SnapPointApi
) : LoginRepository {

    override fun postLogin(email: String, password: String): Flow<LoginResponse> {

        val request: LoginRequest = LoginRequest(
            email = email,
            password = password
        )

        return flowOf(true).map {
            snapPointApi.postLogin(request)
        }
    }

    override fun getLogout(): Flow<Unit> {
        return flowOf(true)
            .map{
                snapPointApi.getLogout()
            }
    }

    override fun postSignup(
        email: String,
        password: String,
        nickname: String
    ): Flow<SignupResponse> {

        val request: SignupRequest = SignupRequest(
            email = email,
            password = password,
            nickname = nickname
        )

        return flowOf(true)
            .map {
                snapPointApi.postSignup(request)
            }
    }
}