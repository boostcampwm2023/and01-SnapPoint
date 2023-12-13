package com.boostcampwm2023.snappoint.presentation.auth.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSignUpBinding
import com.boostcampwm2023.snappoint.presentation.auth.AuthViewModel
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()

        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
            root.post {
                authViewModel.updateBottomSheetHeight(scrollViewSignUp.measuredHeight)
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SignUpEvent.ShowMessage -> {
                            showToastMessage(event.messageResId)
                        }

                        is SignUpEvent.NavigateToSignIn -> {
                            showToastMessage(R.string.signup_fragment_create_account_success)
                            navigateToSignIn()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToSignIn() {
        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }
}