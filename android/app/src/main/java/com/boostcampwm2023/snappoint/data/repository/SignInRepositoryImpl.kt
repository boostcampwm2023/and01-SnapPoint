package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.request.SignInRequest
import com.boostcampwm2023.snappoint.data.remote.model.request.SignupRequest
import com.boostcampwm2023.snappoint.data.remote.model.response.SignInResponse
import com.boostcampwm2023.snappoint.data.remote.model.response.SignupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val snapPointApi: SnapPointApi
) : SignInRepository {

    override fun postSignIn(email: String, password: String): Flow<SignInResponse> {

        val request: SignInRequest = SignInRequest(
            email = email,
            password = password
        )

        return flowOf(true)
            .map {
                snapPointApi.postSignIn(request)
            }
    }

    override fun getSignOut(): Flow<Unit> {
        return flowOf(true)
            .map{
                snapPointApi.getSignOut()
            }
    }

    override fun postSignUp(
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
                snapPointApi.postSignUp(request)
            }
    }
}