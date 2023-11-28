package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.data.remote.SnapPointApi
import com.boostcampwm2023.snappoint.data.remote.model.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val snapPointApi: SnapPointApi
) : LoginRepository {

    override fun postLogin(email: String, password: String): Flow<String> {

        val request: LoginRequest = LoginRequest(
            email = email,
            password = password
        )

        return flow {
            emit(snapPointApi.postLogin(request))
        }
    }
}