package com.boostcampwm2023.snappoint.presentation.viewpost

import android.os.Bundle
import androidx.activity.viewModels
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityViewPostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel

class ViewPostActivity : BaseActivity<ActivityViewPostBinding>(R.layout.activity_view_post) {

    private val viewModel: ViewPostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedIndex = intent.getIntExtra("index", 0)
        viewModel.updateSelectedIndex(selectedIndex)
    }
}