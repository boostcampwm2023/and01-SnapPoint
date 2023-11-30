package com.boostcampwm2023.snappoint.presentation.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSignupBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(R.layout.fragment_signup) {

    private val viewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        loadText()

        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    // TODO(임시데이터!!)
    private fun loadText() {
        with(binding) {
            tilSignUpEmail.editText?.setText(viewModel.uiState.value.email)
            tilSignUpPassword.editText?.setText(viewModel.uiState.value.password)
            tilSignUpPasswordConfirm.editText?.setText(viewModel.uiState.value.passwordConfirm)
            tilSignUpNickname.editText?.setText(viewModel.uiState.value.nickname)
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SignupEvent.Fail -> {
                            showToastMessage(event.toast)
                        }

                        is SignupEvent.Success -> {
                            showToastMessage(R.string.signup_fragment_create_account_success)
                            navigateToLogin()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().popBackStack()
    }
}