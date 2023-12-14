package com.boostcampwm2023.snappoint.presentation.main.setting

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSettingBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: SettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()

        collectViewModelEvent()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel

            btnCreatePost.setOnClickListener {
                findNavController().navigate(R.id.createPostActivity)
            }
        }
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when(event) {
                        is SettingEvent.SignOut -> {
                            alertSignOut()
                        }

                        is SettingEvent.SuccessToSignOut -> {
                            showToastMessage(R.string.setting_fragment_sign_out_success)
                            activityViewModel.navigateSignIn()
                        }

                        is SettingEvent.FailToSignOut -> {
                            showToastMessage(R.string.setting_fragment_sign_out_fail)
                        }

                        is SettingEvent.RemoveSnapPoint -> {
                            activityViewModel.clearPosts()
                        }
                    }
                }
            }
        }
    }

    private fun alertSignOut() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.mipmap.icon_snappoint_launcher_round)
            .setTitle(getString(R.string.setting_fragment_sign_out))
            .setMessage(getString(R.string.setting_fragment_sign_out_alert))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ -> viewModel.signOut() }
            .setNegativeButton(getString(R.string.dialog_no), null)
            .show()
    }
}