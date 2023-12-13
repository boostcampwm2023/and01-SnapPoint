package com.boostcampwm2023.snappoint.data.repository

import com.boostcampwm2023.snappoint.presentation.util.UserInfoPreference
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor (
    private val userInfoPreference: UserInfoPreference
) : UserInfoRepository {

    override fun getEmail(): String {
        return userInfoPreference.getEmail()
    }

    override fun getPassword(): String {
        return userInfoPreference.getPassword()
    }

    override fun setUserAuthData(email: String, password: String) {
        userInfoPreference.setUserAuthData(email, password)
    }

    override fun clearUserAuthData() {
        userInfoPreference.clearUserAuthData()
    }
}