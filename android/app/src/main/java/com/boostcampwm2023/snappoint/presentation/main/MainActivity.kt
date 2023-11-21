package com.boostcampwm2023.snappoint.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.data.remote.model.BlockType
import com.boostcampwm2023.snappoint.data.remote.model.Position
import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.around.AroundFragmentDirections
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback
{
    private val viewModel: MainViewModel by viewModels()
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bnv.setupWithNavController(navController)

        val behavior = BottomSheetBehavior.from(binding.bs)
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        binding.bnv.setOnItemReselectedListener { _ ->
            when (behavior.state) {
                BottomSheetBehavior.STATE_HALF_EXPANDED -> { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
                BottomSheetBehavior.STATE_EXPANDED -> { behavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                else -> { behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED }
            }
        }

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        map.getMapAsync(this)

        initBinding()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.event.collect{event ->
                    when(event){
                        MainActivityEvent.OpenDrawer -> openDrawer()
                    }
                }
            }
        }

        binding.sb.setOnClickListener {
            binding.sv.show()
        }
    }

    private fun openDrawer() {
        binding.dl.open()
    }

    private fun initBinding() {
        with(binding){
            vm = viewModel
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0,0.0), 17.5f))
    }
}