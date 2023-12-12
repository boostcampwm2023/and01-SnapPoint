package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.presentation.util.UserInfoPreference

interface UserInfoRepository {

    fun getEmail(): String
    fun getPassword(): String
    fun setUserAuthData(email: String, password: String)
    fun clearUserAuthData()
}