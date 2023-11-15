package com.boostcampwm2023.snappoint.presentation.createpost

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {

    private val viewModel: CreatePostViewModel by viewModels()
//    private val listAdapter: CreatePostListAdapter by lazy{
//        CreatePostListAdapter(viewModel.uiState.value)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        collectViewModelData()
    }


    fun initBinding() {
        with(binding) {
            vm = viewModel
//            listAdapter.blocks = viewModel.uiState.value.postBlocks.toMutableList()
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            viewModel.event.collect{event ->
                when(event){
                    is CreatePostEvent.ShowMessage -> {showToastMessage(event.resId)}
                }
            }

        }
    }

    private fun showToastMessage(resId: Int) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_LONG).show()
    }
}