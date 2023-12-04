package com.boostcampwm2023.snappoint.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityAuthBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }



}