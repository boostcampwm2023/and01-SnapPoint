package com.boostcampwm2023.snappoint.presentation.util

object UserInfo {

    private var email: String = ""

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }
}