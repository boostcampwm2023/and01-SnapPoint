package com.boostcampwm2023.snappoint.presentation.util

import android.util.Log
import android.util.Patterns

object TextVerificationUtil {

    private val passwordLength: Regex = Regex(".{8,16}")
    private val passwordCharacter: Regex = Regex(".*[a-zA-Z]+.*")
    private val passwordSpecial: Regex = Regex(".*[!@#$%^&*()_-]+.*")
    private val passwordNumber: Regex = Regex(".*[0-9]+.*")

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        if (passwordLength.matches(password)) Log.d("LOG", "R1 pass")
        if (passwordCharacter.matches(password)) Log.d("LOG", "R2 pass")
        if (passwordSpecial.matches(password)) Log.d("LOG", "R3 pass")
        if (passwordNumber.matches(password)) Log.d("LOG", "R4 pass")
        return (passwordLength.matches(password)
                && passwordCharacter.matches(password)
                && passwordSpecial.matches(password)
                && passwordNumber.matches(password))
    }
}