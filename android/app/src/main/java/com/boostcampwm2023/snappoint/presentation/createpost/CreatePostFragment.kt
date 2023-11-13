package com.boostcampwm2023.snappoint.presentation.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment

class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }
}