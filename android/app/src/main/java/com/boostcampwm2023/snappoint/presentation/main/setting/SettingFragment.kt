package com.boostcampwm2023.snappoint.presentation.main.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSettingBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.createpost.CreatePostActivity
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
                val intent = Intent(requireContext(), CreatePostActivity::class.java)
                startActivity(intent)
            }
            binding.btnGetPostInRoom.setOnClickListener {
                viewModel.getSavedPost()
            }
        }
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when(event) {
                        is SettingEvent.SignOut -> {
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
}