package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenUtil @Inject constructor(@ApplicationContext context: Context) {

    private val preferences = context.getSharedPreferences("mPref", MODE_PRIVATE)

    var accessToken: String
        get() = preferences.getString("accessToken", "")!!
        set(token) {
            preferences.edit().putString("token", token).apply()
        }

    var refreshToken: String
        get() = preferences.getString("refreshToken", "")!!
        set(token) {
            preferences.edit().putString("token", token).apply()
        }
}