package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentClusterPreviewBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClusterPreviewFragment : BaseFragment<FragmentClusterPreviewBinding>(R.layout.fragment_cluster_preview) {

    private val clusterPreviewViewModel: ClusterPreviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: ClusterPreviewFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onClusterPreviewShowing()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        collectViewModelData()
        updatePost()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.onClusterPreviewClosing()
    }

    private fun initBinding() {
        with(binding) {
            vm = clusterPreviewViewModel
            root.post {
                rcvClusterList.layoutParams.height =
                    mainViewModel.bottomSheetHeight - glTop.top
            }
        }
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                clusterPreviewViewModel.event.collect { event ->
                    when (event) {
                        is ClusterPreviewEvent.NavigateClusterImage -> {
                            navigateToPreview(event.index)
                        }
                    }
                }
            }
        }
    }

    private fun updatePost() {
        val posts = mainViewModel.postState.value
        val list = mutableListOf<PostBlockState>()
        args.tags.forEach { tag ->
            posts.find { it.uuid == tag.postUuid}?.postBlocks?.find { it.uuid == tag.blockUuid }
                ?.let { list.add(it) }
        }
        clusterPreviewViewModel.updatePostList(list.toList())
    }

    private fun navigateToPreview(index: Int) {
        val tag = args.tags[index]
        mainViewModel.onMarkerClicked(tag)
    }
}