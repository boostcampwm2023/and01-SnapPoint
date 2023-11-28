package com.boostcampwm2023.snappoint.presentation.viewpost

import android.os.Bundle
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

        val selectedIndex = intent.getIntExtra("index", 0)
        viewModel.updateSelectedIndex(selectedIndex)

        lifecycleScope.launch {
            viewModel.event.collect {
                when (it) {
                    ViewPostEvent.finishActivity -> { finish() }
                }
            }
        }
    }
}