package com.boostcampwm2023.snappoint.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.doOnLayout
import androidx.core.view.marginTop
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.addImageMarker
import com.boostcampwm2023.snappoint.presentation.util.pxFloat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback,
    OnMarkerClickListener
{
    private val viewModel: MainViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment).findNavController()
    }

    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> by lazy {
        BottomSheetBehavior.from(binding.bs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initBottomSheetWithNavigation()

        initMapFragment()

        collectViewModelData()

        setBottomNavigationEvent()

        binding.fab.setOnClickListener {
            openPreviewFragment()
        }
    }

    private fun initMapFragment() {
        val map: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        map.getMapAsync(this)

    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
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

                            is MainActivityEvent.NavigatePreview -> {
                                openPreviewFragment()
                            }
                        }
                    }
                }
                launch {
                    viewModel.uiState.collect { uiState ->
                        updateMarker(uiState.snapPoints)
                    }
                }
            }
        }
    }

    private fun updateMarker(snapPoints: List<SnapPointState>) {
        lifecycleScope.launch {
            while (googleMap == null) {
                delay(100)
            }
            val selectedIndex = viewModel.uiState.value.selectedIndex
            googleMap?.let { map ->
                map.clear()
                snapPoints.forEachIndexed { postIndex, snapPointState ->
                    if(postIndex == selectedIndex){
                        drawRoutes(selectedIndex)
                    }
                    snapPointState.markerOptions.forEachIndexed { snapPointIndex, markerOptions ->
                        val focused =
                            (postIndex == viewModel.uiState.value.selectedIndex) && (snapPointIndex == viewModel.uiState.value.focusedIndex)
                        map.addImageMarker(
                            context = this@MainActivity,
                            markerOptions = markerOptions,
                            uri = "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcRoT6NNDUONDQmlthWrqIi_frTjsjQT4UZtsJsuxqxLiaFGNl5s3_pBIVxS6-VsFUP_",
                            tag = SnapPointTag(postIndex = postIndex, snapPointIndex = snapPointIndex),
                            focused = focused
                        )
                    }
                }
            }
        }
    }

    private fun drawRoutes(postIndex: Int) {
        val polylineOptions = PolylineOptions().color(getColor(R.color.error80)).width(3.pxFloat()).pattern(listOf(Dash(20f), Gap(20f)))
        val positionList = viewModel.uiState.value.posts[postIndex].postBlocks.filterNot { it is PostBlockState.STRING }.map{ block ->
            when (block) {
                is PostBlockState.IMAGE -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.VIDEO -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.STRING -> TODO()
            }
        }
        polylineOptions.addAll(positionList)
        googleMap?.addPolyline(polylineOptions)
    }

    private fun initBottomSheetWithNavigation() {
        binding.bnv.setupWithNavController(navController)
        bottomSheetBehavior.halfExpandedRatio = 0.45f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        binding.sb.doOnLayout {
            bottomSheetBehavior.expandedOffset = binding.sb.height + binding.sb.marginTop * 2
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                viewModel.onBottomSheetChanged(state == BottomSheetBehavior.STATE_EXPANDED)
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })

        binding.bnv.setOnItemReselectedListener { _ ->
            if (viewModel.uiState.value.isPreviewFragmentShowing) {
                return@setOnItemReselectedListener
            }

            bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                else -> BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
    }

    private fun setBottomNavigationEvent() {
        binding.bnv.setOnItemSelectedListener { menuItem ->
            navController.popBackStack()
            navController.navigate(menuItem.itemId)
            true
        }
    }

    private fun openPreviewFragment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        navController.navigate(R.id.previewFragment)
    }

    private fun openDrawer() {
        binding.dl.open()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setOnMarkerClickListener(this)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(10.0, 10.0), 10f))
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.onMarkerClicked(marker.tag as SnapPointTag)
        return true
    }
}