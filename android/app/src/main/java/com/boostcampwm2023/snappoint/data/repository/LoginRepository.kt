package com.boostcampwm2023.snappoint.data.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun postLogin(email: String, password: String): Flow<String>
}