package com.boostcampwm2023.snappoint.presentation.createpost

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {

    private val viewModel: CreatePostViewModel by viewModels()
    private val listAdapter: CreatePostListAdapter by lazy{
        CreatePostListAdapter(viewModel.uiState.value)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModelData()
    }

    override fun initBinding() {
        with(binding) {
            vm = viewModel
            rcvPostBlock.adapter = listAdapter
            listAdapter.blocks = viewModel.uiState.value.postBlocks.toMutableList()
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                if (it.postBlocks.size > listAdapter.blocks.size) {
                    listAdapter.blocks = it.postBlocks.toMutableList()
                    listAdapter.notifyItemInserted(it.postBlocks.size)
                    binding.rcvPostBlock.scrollToPosition(it.postBlocks.size - 1)
                }
            }
        }
    }
}