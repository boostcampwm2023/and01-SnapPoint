package com.boostcampwm2023.snappoint.presentation.main.preview

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentPreviewBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import com.boostcampwm2023.snappoint.presentation.util.Constants
import com.google.android.material.carousel.CarouselSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewFragment : BaseFragment<FragmentPreviewBinding>(R.layout.fragment_preview) {

    private val previewViewModel: PreviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val snapHelper: CarouselSnapHelper = CarouselSnapHelper()
    private val layoutManager: LayoutManager by lazy { binding.rcvPreview.layoutManager!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onPreviewFragmentShowing()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        initViewSize()

        collectViewModelData()
        collectViewModelEvent()

        setScrollEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.onPreviewFragmentClosing()
    }

    private fun initBinding() {
        with(binding) {
            vm = previewViewModel
            snapHelper.attachToRecyclerView(rcvPreview)
        }
    }

    private fun initViewSize() {
        with(binding) {
            root.post {
                rcvPreview.layoutParams.height =
                    mainViewModel.bottomSheetHeight - glTop.top
            }
        }
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mainViewModel.markerState.collect{
                    if (it.selectedIndex > -1) {
                        previewViewModel.updatePost(mainViewModel.postState.value[it.selectedIndex])
                    }
                    moveScroll(it.focusedIndex)
                }
            }
        }
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                previewViewModel.event.collect { event ->
                    when(event) {
                        is PreviewEvent.NavigateViewPost -> {
                            navigateViewPost()
                        }
                    }
                }
            }
        }
    }

    private fun moveScroll(focusedSnapPointIndex: Int) {
        if (binding.rcvPreview.scrollState == RecyclerView.SCROLL_STATE_IDLE)
            layoutManager.scrollToPosition(focusedSnapPointIndex)
    }

    private fun setScrollEvent() {
        binding.rcvPreview.setOnScrollChangeListener { _, _, _, scrollX, _ ->
            if (scrollX == 0) {
                layoutManager.scrollToPosition(mainViewModel.markerState.value.focusedIndex)
                return@setOnScrollChangeListener
            }
            val currentFocusImageIndex =
                layoutManager.getPosition(
                    snapHelper.findSnapView(layoutManager) ?: return@setOnScrollChangeListener
                )

            if (mainViewModel.markerState.value.focusedIndex != currentFocusImageIndex) {
                mainViewModel.focusOfImageMoved(currentFocusImageIndex)
            }
        }
    }

    private fun navigateViewPost() {
        findNavController().navigate(
            R.id.action_previewFragment_to_viewPostActivity,
            bundleOf(Constants.UUID_BUNDLE_KEY to previewViewModel.uiState.value.uuid)
        )
    }
}