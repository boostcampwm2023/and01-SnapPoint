package com.boostcampwm2023.snappoint.presentation.main.clusterlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentClusterListBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.main.MainViewModel

class ClusterListFragment : BaseFragment<FragmentClusterListBinding>(R.layout.fragment_cluster_list) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: ClusterListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onPreviewFragmentShowing()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.onPreviewFragmentClosing()
    }


}