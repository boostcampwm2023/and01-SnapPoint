package com.boostcampwm2023.snappoint.presentation.main

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.util.addImageMarker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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

    private val navController: NavController =
        (supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment).findNavController()

    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> by lazy {
        BottomSheetBehavior.from(binding.bs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initBottomSheetWithNavigation()

        initMapApi()

        collectViewModelData()

        // FAB 구현할 때 삭제해 주세요!!
        setFabClickEvent()
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
                            MainActivityEvent.OpenDrawer -> {
                                openDrawer()
                            }
                            MainActivityEvent.NavigatePrev -> {
                                navController.popBackStack()
                            }
                            MainActivityEvent.NavigateClose -> {
                                navController.popBackStack()
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
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
                    it.markerOptions.forEach { markerOptions ->
                        map.addImageMarker(
                            context = this@MainActivity,
                            markerOptions = markerOptions,
                            uri = "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcRoT6NNDUONDQmlthWrqIi_frTjsjQT4UZtsJsuxqxLiaFGNl5s3_pBIVxS6-VsFUP_")
                    }
                }
            }
        }
    }

    private fun initBottomSheetWithNavigation() {
        binding.bnv.setupWithNavController(navController)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        binding.bnv.setOnItemReselectedListener { _ ->
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_HALF_EXPANDED -> { bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
                BottomSheetBehavior.STATE_EXPANDED -> { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                else -> { bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED }
            }
        }
    }

    private fun setFabClickEvent() {
        binding.fab.setOnClickListener {
            openPreviewFragment()
        }
    }

    private fun openPreviewFragment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        navController.navigate(R.id.action_aroundFragment_to_previewFragment)
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(10.0,10.0), 10f))
    }
}