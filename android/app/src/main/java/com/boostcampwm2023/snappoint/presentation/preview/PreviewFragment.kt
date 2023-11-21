package com.boostcampwm2023.snappoint.presentation.preview

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentPreviewBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.createpost.PositionState
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlockState

class PreviewFragment : BaseFragment<FragmentPreviewBinding>(R.layout.fragment_preview) {

    private val viewModel: PreviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.rcvPreview.adapter = PreviewAdapter(viewModel.uiState.value.blocks)
    }
}