package com.boostcampwm2023.snappoint.presentation.auth

import android.os.Bundle
import android.widget.LinearLayout
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityAuthBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fcv_auth) as NavHostFragment).findNavController()
    }
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> by lazy {
        BottomSheetBehavior.from(binding.bsAuth)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                finish()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        initBinding()

        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
            root.post {
                viewModel.updateFragmentHeight(root.measuredHeight, dragHandleAuth.measuredHeight)
            }

            val behavior = BottomSheetBehavior.from(bsAuth)
            behavior.isDraggable = true

            clAuth.bringToFront()
            bsAuth.bringToFront()

            btnAuthSignInEmail.setOnClickListener {
                viewModel.activateBottomSheet()

                if(navController.currentDestination?.id != R.id.signInFragment) {
                    navController.navigate(R.id.action_signUpFragment_to_signInFragment)
                } else {
                    expandBottomSheetHalf()
                }
            }

            tvAuthSignUp.setOnClickListener {
                viewModel.activateBottomSheet()

                if(navController.currentDestination?.id != R.id.signUpFragment) {
                    navController.navigate(R.id.action_signInFragment_to_signUpFragment)
                } else {
                    expandBottomSheetHalf()
                }
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect {
                    expandBottomSheetHalf()
                }
            }
        }
    }

    private fun expandBottomSheetHalf() {
        with(viewModel.uiState.value) {
            if (isBottomSheetActivated) {
                val ratio = ((bottomSheetHeight + handleHeight) / fragmentHeight.toFloat())
                if (ratio >= 1F) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else if (ratio > 0F) {
                    bottomSheetBehavior.halfExpandedRatio = ratio
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }
    }
}