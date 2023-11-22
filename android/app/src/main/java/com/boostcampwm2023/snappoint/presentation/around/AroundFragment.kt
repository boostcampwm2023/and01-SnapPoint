package com.boostcampwm2023.snappoint.presentation.around

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
                    mainViewModel.uiState.collect {
                        aroundViewModel.updatePosts(it.posts)
                    }
                }
                launch {
                    aroundViewModel.event.collect { event ->
                        when (event) {
                            is AroundEvent.ShowSnapPointAndRoute -> {
                                mainViewModel.previewButtonClicked(event.index)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initBinding() {
        with(binding) {
            vm = aroundViewModel
        }
    }

}