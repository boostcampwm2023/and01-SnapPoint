package com.boostcampwm2023.snappoint.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
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
import com.boostcampwm2023.snappoint.presentation.util.Constants
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.LOCATION_PERMISSION_REQUEST_CODE
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.isMyLocationGranted
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.isPermissionGranted
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.locationPermissionRequest
import com.boostcampwm2023.snappoint.presentation.util.addImageMarker
import com.boostcampwm2023.snappoint.presentation.util.pxFloat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initBottomSheetWithNavigation()

        initMapFragment()

        collectViewModelData()

        setBottomNavigationEvent()

        initLocationData()


    }



    private fun initLocationData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d("TAG", "onLocationResult: ${p0}")
            }
        }

    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
                                moveCameraToFitScreen()
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
        val positionList = viewModel.uiState.value.posts[postIndex].postBlocks.filterNot { it is PostBlockState.TEXT }.map{ block ->
            when (block) {
                is PostBlockState.IMAGE -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.VIDEO -> {
                    LatLng(block.position.latitude, block.position.longitude)
                }

                is PostBlockState.TEXT -> TODO()
            }
        }
        polylineOptions.addAll(positionList)
        googleMap?.addPolyline(polylineOptions)
    }

    private fun moveCameraToFitScreen() {
        val postIndex = viewModel.uiState.value.selectedIndex
        val snapPoints = viewModel.uiState.value.snapPoints[postIndex]
        val positions = snapPoints.markerOptions.map { it.position }

        // 아프리카 적도기니를 기준
        // latitude: 북반구(+) 남반구(-)
        // longitude: 서쪽(-) 동쪽(+)
        val topOfBound: Double = positions.maxOf { it.latitude }
        val bottomOfBound: Double = positions.minOf { it.latitude }
        val leftOfBound: Double = positions.minOf { it.longitude }
        val rightOfBound: Double = positions.maxOf { it.longitude }

        // 단위: Pixel
        val padding: Int = maxOf(binding.topAppBar.height, binding.sb.height)

        val heightOfMap: Double = binding.fcvMainMap.height.toDouble()
        val heightOfLayout: Double = binding.cl.height.toDouble()

        val topAppBarRatio: Double = padding / heightOfMap
        val bottomNavViewRatio: Double = binding.bnv.height / heightOfMap
        val bottomSheetRatio: Double = (Constants.BOTTOM_SHEET_HALF_EXPANDED_RATIO * heightOfLayout
                + binding.dragHandle.height) / heightOfMap
        val visibleRatio: Double = 1.0 - (topAppBarRatio + bottomNavViewRatio + bottomSheetRatio)


        val heightOfBound: Double = topOfBound - bottomOfBound

        val newTopOfBound: Double = topOfBound + heightOfBound * topAppBarRatio / visibleRatio
        val newBottomOfBound: Double = bottomOfBound - heightOfBound * (bottomNavViewRatio + bottomSheetRatio) / visibleRatio

        val bound: LatLngBounds = LatLngBounds(
            LatLng(newBottomOfBound, leftOfBound),
            LatLng(newTopOfBound, rightOfBound)
        )

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, padding))
    }

    private fun initBottomSheetWithNavigation() {
        binding.bnv.setupWithNavController(navController)
        bottomSheetBehavior.halfExpandedRatio = Constants.BOTTOM_SHEET_HALF_EXPANDED_RATIO
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
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
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

            fab.setOnClickListener {
                checkPermissionAndMoveCameraToUserLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setOnMarkerClickListener(this)

        checkPermissionAndMoveCameraToUserLocation()

    }

    @SuppressLint("MissingPermission")
    private fun checkPermissionAndMoveCameraToUserLocation() {
        if(this.isMyLocationGranted()){
            fusedLocationClient.lastLocation
                .addOnSuccessListener {location ->
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),17.5f))
                }
        }else{
            locationPermissionRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        if(this.isMyLocationGranted()){
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(LocationRequest.Builder(1000L).build(),
            locationCallback,
            Looper.getMainLooper()
            )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            Toast.makeText(this, "권한 ㄳ염 ", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "내 위치 정보를 사용하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }



    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.onMarkerClicked(marker.tag as SnapPointTag)
        return true
    }
}