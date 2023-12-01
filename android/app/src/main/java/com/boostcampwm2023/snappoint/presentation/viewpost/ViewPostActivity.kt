package com.boostcampwm2023.snappoint.presentation.viewpost

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityViewPostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewPostActivity : BaseActivity<ActivityViewPostBinding>(R.layout.activity_view_post) {

    private val viewModel: ViewPostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPostData()

        collectViewModelData()
    }

    private fun getPostData() {
        val uuid = intent.getStringExtra("uuid") ?: ""
        viewModel.loadPost(uuid)
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            viewModel.event.collect {
                when (it) {
                    ViewPostEvent.FinishActivity -> {
                        finish()
                    }
                }
            }
        }
    }
}