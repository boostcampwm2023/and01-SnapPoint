package com.boostcampwm2023.snappoint.presentation.main.around

import android.content.Intent
import android.os.Bundle
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
import com.boostcampwm2023.snappoint.presentation.viewpost.ViewPostActivity
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
                                navigateToViewPost(event.index)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToViewPost(index: Int) {
        val intent = Intent(requireContext(), ViewPostActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent)
    }

    private fun initBinding() {
        with(binding) {
            vm = aroundViewModel
        }
    }
}