package com.boostcampwm2023.snappoint.presentation.util

import android.util.Log
import android.util.Patterns

object TextVerificationUtil {

    private val passwordLength: Regex = Regex(".{8,16}")
    private val passwordLowerCase: Regex = Regex(".*[A-Z]+.*")
    private val passwordUpperCase: Regex = Regex(".*[a-z]+.*")
    private val passwordSpecial: Regex = Regex(".*[!@#$%^&*()_-]+.*")
    private val passwordNumber: Regex = Regex(".*[0-9]+.*")

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return (passwordLength.matches(password)
                && passwordLowerCase.matches(password)
                && passwordUpperCase.matches(password)
                && passwordSpecial.matches(password)
                && passwordNumber.matches(password))
    }
}