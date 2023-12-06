package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSubscriptionBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>(R.layout.fragment_subscription) {

    private val viewModel: SubscriptionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val navController: NavController by lazy { findNavController() }
    private var isOnResume: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()

        loadPostsFromLocal()

        updatePostsUi()
        collectViewModelData()
    }

    override fun onResume() {
        super.onResume()
        isOnResume = true
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    private fun loadPostsFromLocal() {
        mainViewModel.loadLocalPost()
    }

    private fun updatePostsUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.postState.collect { posts ->
                    viewModel.updatePosts(posts)
                }
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when(event) {
                        is SubscriptionEvent.ShowSnapPointAndRoute -> {
                            mainViewModel.previewButtonClicked(event.index)
                        }

                        is SubscriptionEvent.NavigateViewPost -> {
                            navigateToViewPost(
                                viewModel.uiState.value.posts[event.index].uuid
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateToViewPost(uuid: String) {
        navController.run {
            if (currentDestination?.id != R.id.viewPostActivity && isOnResume) {
                isOnResume = false
                navigate(
                    R.id.action_subscriptionFragment_to_viewPostActivity,
                    bundleOf("uuid" to uuid, "isLocalPost" to true)
                )
            }
        }
    }
}