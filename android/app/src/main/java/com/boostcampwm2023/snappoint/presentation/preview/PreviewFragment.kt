package com.boostcampwm2023.snappoint.presentation.preview

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentPreviewBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import com.google.android.material.carousel.CarouselSnapHelper
import kotlinx.coroutines.launch

class PreviewFragment : BaseFragment<FragmentPreviewBinding>(R.layout.fragment_preview) {

    private val previewViewModel: PreviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val snapHelper: CarouselSnapHelper = CarouselSnapHelper()
    private val layoutManager: LayoutManager by lazy { binding.rcvPreview.layoutManager!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        collectViewModelData()

        binding.rcvPreview.setOnScrollChangeListener { _, _, _, _, _ ->
            Log.d(
                "LOG",
                "IDX: ${layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)}"
            )
        }
    }

    private fun initBinding() {
        with(binding) {
            vm = previewViewModel
            snapHelper.attachToRecyclerView(rcvPreview)
        }
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mainViewModel.uiState.collect{
                    previewViewModel.updatePost(it.posts[0])
                }
            }
        }
    }
}