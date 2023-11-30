package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SignInUtil @Inject constructor(@ApplicationContext context: Context) {

    private val preferences = context.getSharedPreferences("mPref", MODE_PRIVATE)
    var email: String
        get() = preferences.getString("email", "")!!
        set(email) {
            preferences.edit().putString("email", email).apply()
        }
    var password: String
        get() = preferences.getString("password", "")!!
        set(password) {
            preferences.edit().putString("password", password).apply()
        }
}