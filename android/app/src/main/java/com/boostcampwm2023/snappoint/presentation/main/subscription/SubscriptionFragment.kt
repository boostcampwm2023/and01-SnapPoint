package com.boostcampwm2023.snappoint.presentation.main.subscription

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentSubscriptionBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.createpost.CreatePostActivity

class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>(R.layout.fragment_subscription) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
    }

    private fun initBinding() {
        binding.btnCreatePost.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }
    }
}