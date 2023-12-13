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
import com.boostcampwm2023.snappoint.presentation.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>(R.layout.fragment_subscription) {

    private val viewModel: SubscriptionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val navController: NavController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()

        displayLocalSnapPoints()

        updatePostsUi()
        collectViewModelData()
    }

    override fun onResume() {
        super.onResume()
        loadPostsFromLocal()
        setViewPostStateClosed()
    }

    override fun onDestroy() {
        super.onDestroy()
        displayRemoteSnapPoints()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    private fun displayLocalSnapPoints() {
        mainViewModel.displayLocalSnapPoints()
    }

    private fun displayRemoteSnapPoints() {
        mainViewModel.displayRemoteSnapPoints()
    }

    private fun loadPostsFromLocal() {
        mainViewModel.loadLocalPost()
    }

    private fun setViewPostStateClosed() {
        viewModel.setViewPostState(false)
    }

    private fun updatePostsUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.localPostState.collect { posts ->
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
        if (navController.currentDestination?.id != R.id.viewPostActivity
            && viewModel.uiState.value.isViewPostOpened.not()
        ) {
            viewModel.setViewPostState(true)
            navController.navigate(
                R.id.action_subscriptionFragment_to_viewPostActivity,
                bundleOf(
                    Constants.UUID_BUNDLE_KEY to uuid,
                    Constants.IS_LOCAL_POST_BUNDLE_KEY to true
                )
            )
        }
    }
}