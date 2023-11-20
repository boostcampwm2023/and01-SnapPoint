package com.boostcampwm2023.snappoint.presentation.around

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentAroundBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AroundFragment : BaseFragment<FragmentAroundBinding>(R.layout.fragment_around) {

    private val viewModel: AroundViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }
}