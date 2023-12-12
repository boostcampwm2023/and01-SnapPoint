package com.boostcampwm2023.snappoint.presentation.auth

import android.os.Bundle
import android.widget.LinearLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
            root.post {
                viewModel.updateFragmentHeight(root.measuredHeight)
            }

            val behavior = BottomSheetBehavior.from(bsAuth)
            behavior.isDraggable = true

            clAuth.bringToFront()
            bsAuth.bringToFront()

            btnAuthSignInEmail.setOnClickListener {
                if(navController.currentDestination?.id != R.id.signInFragment) {
                    navController.navigate(R.id.action_signUpFragment_to_signInFragment)
                } else {
                    expandBottomSheetHalf()
                }
            }

            tvAuthSignUp.setOnClickListener {
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
            bottomSheetBehavior.halfExpandedRatio = bottomSheetHeight / fragmentHeight.toFloat()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }
}