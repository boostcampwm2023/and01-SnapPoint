package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentPostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.util.Constants
import com.boostcampwm2023.snappoint.presentation.viewpost.ViewPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private val postViewModel: PostViewModel by viewModels()
    private val viewPostViewModel: ViewPostViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()

        updatePostState()
        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = postViewModel
            activityVm = viewPostViewModel
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            postViewModel.event.collect { event ->
                when (event) {
                    PostEvent.NavigatePrev -> {
                        if (!findNavController().popBackStack()) {
                            viewPostViewModel.finishPostView()
                        }
                    }

                    PostEvent.SavePost -> {
                        saveCurrentPostToLocal()
                    }

                    PostEvent.DeletePost -> {
                        deleteCurrentPostFromLocal()
                    }

                    is PostEvent.MenuItemClicked -> {
                        invokeMenuClickEvent(event.itemId)
                    }
                }
            }
        }
    }

    private fun updatePostState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewPostViewModel.post.collect { post ->
                    postViewModel.updateLikeMarkState(post.uuid)
                }
            }
        }
    }

    private fun saveCurrentPostToLocal() {
        postViewModel.saveCurrentPostToLocal(viewPostViewModel.post.value)
    }

    private fun deleteCurrentPostFromLocal() {
        postViewModel.deleteCurrentPostFromLocal(viewPostViewModel.post.value.uuid)
    }

    private fun invokeMenuClickEvent(itemId: Int) {
        when(itemId) {
            R.id.post_edit -> {
                navigateEditPost()
            }
            R.id.post_delete -> {

            }
            R.id.post_report -> {

            }
            R.id.post_ignore -> {

            }
        }
    }

    private fun navigateEditPost() {
        findNavController().navigate(
            R.id.action_postFragment_to_createPostActivity2,
            bundleOf(
                Constants.POST_BUNDLE_KEY to Json.encodeToString(viewPostViewModel.post.value)
            )
        )
    }
}