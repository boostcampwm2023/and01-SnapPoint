package com.boostcampwm2023.snappoint.presentation.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.boostcampwm2023.snappoint.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_test)
    }
}