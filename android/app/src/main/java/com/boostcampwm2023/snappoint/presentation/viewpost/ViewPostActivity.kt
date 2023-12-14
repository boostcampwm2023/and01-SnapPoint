package com.boostcampwm2023.snappoint.presentation.viewpost

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityViewPostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewPostActivity : BaseActivity<ActivityViewPostBinding>(R.layout.activity_view_post) {

    private val viewModel: ViewPostViewModel by viewModels()
    private val args: ViewPostActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collectViewModelData()
    }

    override fun onResume() {
        super.onResume()

        getPostData()
    }

    private fun getPostData() {
        if(args.isLocalPost) {
            viewModel.loadLocalPost(args.uuid)
        }else {
            viewModel.loadPost(args.uuid)
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            viewModel.event.collect {
                when (it) {
                    ViewPostEvent.SuccessToDeletePost -> {
                        showToastMessage(R.string.view_post_activity_success_to_delete_post)
                    }

                    ViewPostEvent.FailToDeletePost -> {
                        showToastMessage(R.string.view_post_activity_fail_to_delete_post)
                    }

                    ViewPostEvent.FinishActivity -> {
                        finish()
                    }
                }
            }
        }
    }
}