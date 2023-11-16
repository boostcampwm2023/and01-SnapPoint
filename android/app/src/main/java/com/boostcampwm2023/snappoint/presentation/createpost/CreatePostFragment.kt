package com.boostcampwm2023.snappoint.presentation.createpost

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {

    private val pickImage = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.addImageBlock(uri)
        } else {
            println("uri is null.")
        }
    }
    private val viewModel: CreatePostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        collectViewModelData()
    }


    fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            viewModel.event.collect{event ->
                when(event){
                    is CreatePostEvent.ShowMessage -> {showToastMessage(event.resId)}
                    is CreatePostEvent.SelectImageFromLocal -> {
                        pickImage.launch(
                            PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                        )
                    }
                }
            }

        }
    }

    private fun showToastMessage(resId: Int) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_LONG).show()
    }
}