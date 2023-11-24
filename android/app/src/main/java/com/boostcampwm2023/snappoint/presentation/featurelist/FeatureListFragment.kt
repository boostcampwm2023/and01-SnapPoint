package com.boostcampwm2023.snappoint.presentation.featurelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentFeatureListBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment


class FeatureListFragment : BaseFragment<FragmentFeatureListBinding>(R.layout.fragment_feature_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNavigateCreatePostActivity.setOnClickListener {
            findNavController().navigate(FeatureListFragmentDirections.actionToCreatePostFragment())
        }
    }
}