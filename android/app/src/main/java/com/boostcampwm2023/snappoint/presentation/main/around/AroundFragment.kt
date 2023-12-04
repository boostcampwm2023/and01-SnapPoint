package com.boostcampwm2023.snappoint.presentation.main.around

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentAroundBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AroundFragment : BaseFragment<FragmentAroundBinding>(R.layout.fragment_around) {

    private val aroundViewModel: AroundViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        collectViewModelData()
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainViewModel.postState.collect {
                        aroundViewModel.updatePosts(it)
                    }
                }
                launch {
                    aroundViewModel.event.collect { event ->
                        when (event) {
                            is AroundEvent.ShowSnapPointAndRoute -> {
                                mainViewModel.previewButtonClicked(event.index)
                            }

                            is AroundEvent.NavigateViewPost -> {
                                val uuid = mainViewModel.postState.value[event.index].uuid
                                navigateToViewPost(uuid)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToViewPost(uuid: String) {
        val bundle = bundleOf("uuid" to uuid)
        findNavController().navigate(R.id.action_aroundFragment_to_viewPostActivity, bundle)
    }

    private fun initBinding() {
        with(binding) {
            vm = aroundViewModel
        }
    }
}