package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SignInUtil @Inject constructor(@ApplicationContext context: Context) {

    private val prefName: String = "mPref"
    private val emailKey: String = "email"
    private val passwordKey: String = "password"

    private val preferences = context.getSharedPreferences(prefName, MODE_PRIVATE)

    fun getEmail(): String {
        return preferences.getString(emailKey, "")!!
    }

    fun getPassword(): String {
        return preferences.getString(passwordKey, "")!!
    }

    fun setUserAuthData(email: String, password: String) {
        UserInfo.setEmail(email)
        preferences.edit().putString(emailKey, email).apply()
        preferences.edit().putString(passwordKey, password).apply()
    }

    fun clearUserAuthData() {
        preferences.edit().putString("email", "").apply()
        preferences.edit().putString("password", "").apply()
    }
}