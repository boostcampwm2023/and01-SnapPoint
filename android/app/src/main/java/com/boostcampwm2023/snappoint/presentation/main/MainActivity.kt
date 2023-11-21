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
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback
{
    private val viewModel: MainViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initBottomSheetWithNavigation()

        initMapApi()

        collectViewModelData()

    }

    private fun initMapApi() {
        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        map.getMapAsync(this)
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){

                launch {
                    viewModel.event.collect{event ->
                        when(event){
                            MainActivityEvent.OpenDrawer -> openDrawer()
                        }
                    }
                }
                launch {
                    viewModel.uiState.collect{uiState ->
                        updateMarker(uiState.snapPoints)
                    }
                }
            }
        }
    }

    private fun updateMarker(snapPoints: List<SnapPointState>) {
        lifecycleScope.launch {
            while(googleMap == null){
                delay(1000)

            }
            googleMap?.let { map ->
                map.clear()
                snapPoints.forEach {
                    it.markerOptions.forEach {
                        map.addMarker(it)
                    }
                }
            }
        }
    }

    private fun initBottomSheetWithNavigation() {
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
        this.googleMap = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(10.0,10.0)))
    }
}