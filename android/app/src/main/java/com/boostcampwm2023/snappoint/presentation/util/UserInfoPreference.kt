package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserInfoPreference @Inject constructor(@ApplicationContext context: Context) {

    private val preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    fun getEmail(): String {
        return preferences.getString(EMAIL_KEY, DEFAULT_VALUE)!!
    }

    fun getPassword(): String {
        return preferences.getString(PASSWORD_KEY, DEFAULT_VALUE)!!
    }

    fun setUserAuthData(email: String, password: String) {
        UserInfo.setEmail(email)
        preferences.edit().putString(EMAIL_KEY, email).apply()
        preferences.edit().putString(PASSWORD_KEY, password).apply()
    }

    fun clearUserAuthData() {
        preferences.edit().putString("email", "").apply()
        preferences.edit().putString("password", "").apply()
    }

    companion object {
        private const val PREF_NAME: String = "mPref"
        private const val EMAIL_KEY: String = "email"
        private const val PASSWORD_KEY: String = "password"
        private const val DEFAULT_VALUE: String = ""
    }
}