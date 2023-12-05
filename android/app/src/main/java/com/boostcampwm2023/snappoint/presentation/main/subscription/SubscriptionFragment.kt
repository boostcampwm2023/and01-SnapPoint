package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSubscriptionBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.createpost.CreatePostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>(R.layout.fragment_subscription) {

    private val viewModel: SubscriptionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
    }

    private fun initBinding() {
        binding.btnCreatePost.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }
        binding.btnGetPostInRoom.setOnClickListener {
            viewModel.getSavedPost()
        }
    }
}