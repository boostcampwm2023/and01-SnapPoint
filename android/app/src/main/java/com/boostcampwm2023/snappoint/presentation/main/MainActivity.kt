package com.boostcampwm2023.snappoint.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import com.boostcampwm2023.snappoint.presentation.auth.AuthActivity
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.SnapPointTag
import com.boostcampwm2023.snappoint.presentation.util.Constants
import com.boostcampwm2023.snappoint.presentation.util.Constants.API_KEY
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.LOCATION_PERMISSION_REQUEST_CODE
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.isMyLocationGranted
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.isPermissionGranted
import com.boostcampwm2023.snappoint.presentation.util.PermissionUtil.locationPermissionRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity(
) : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var placesClient: PlacesClient
    private val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private val geocoder: Geocoder by lazy { Geocoder(applicationContext) }
    private lateinit var mapManager: MapManager

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

        initPlacesClient()

        initBinding()

        initBottomSheetWithNavigation()

        initMapFragment()

        collectViewModelData()

        setBottomNavigationEvent()

        initLocationData()
    }

    private fun initPlacesClient() {
        Places.initializeWithNewPlacesApiEnabled(applicationContext, API_KEY)
        placesClient = Places.createClient(this)
    }

    private fun initLocationData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
            }
        }

        cachingBottomSheetSize()
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun initMapFragment() {
        val map: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        mapManager = MapManager(viewModel, this)
        map.getMapAsync(mapManager)
    }

    private fun cachingBottomSheetSize() {
        with(binding) {
            root.post {
                viewModel.bottomSheetHeight =
                    (cl.height * Constants.BOTTOM_SHEET_HALF_EXPANDED_RATIO).toInt()
            }
        }
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
                                if (navController.currentDestination?.id != R.id.previewFragment) {
                                    openPreviewFragment()
                                }
                                moveCameraToFitScreen()
                            }

                            is MainActivityEvent.MoveCameraToAddress -> {
                                val address = viewModel.searchViewUiState.value.texts[event.index]
                                moveCameraToAddress(address)
                            }

                            is MainActivityEvent.NavigateSignIn -> {
                                navigateAuthActivity()
                            }

                            MainActivityEvent.CheckPermissionAndMoveCameraToUserLocation -> {
                                checkPermissionAndMoveCameraToUserLocation()
                            }

                            is MainActivityEvent.HalfOpenBottomSheet -> {
                                halfOpenBottomSheetWhenCollapsed()
                            }

                            is MainActivityEvent.GetAroundPostFailed -> {
                                showToastMessage(R.string.get_around_posts_failed)
                            }
                        }
                    }
                }

                launch {

                    viewModel.markerState.collect { markerState ->
                        if (markerState.selectedIndex < 0 || markerState.focusedIndex < 0) {
                            mapManager.removeFocus()
                            return@collect
                        }
                        val block = viewModel.postState.value[markerState.selectedIndex].postBlocks
                            .filterIsInstance<PostBlockState.IMAGE>()[markerState.focusedIndex]
                        mapManager.changeSelectedMarker(block, SnapPointTag(markerState.selectedIndex, markerState.focusedIndex))

                        if (mapManager.prevSelectedIndex != markerState.selectedIndex) {
                            mapManager.changeRoute(viewModel.postState.value[markerState.selectedIndex].postBlocks)
                        }
                    }
                }

                launch {
                    viewModel.postState.collect { postState ->
                        while (mapManager.googleMap == null) { delay(100) }
                        mapManager.updateMarkers(postState)
                    }
                }
            }
        }
    }

    private fun moveCameraToFitScreen() {
        val postIndex = viewModel.markerState.value.selectedIndex
        val snapPoints = viewModel.postState.value[postIndex].postBlocks.filterIsInstance<PostBlockState.IMAGE>()
        val positions = snapPoints.map { it.position }

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

        val bound = LatLngBounds(
            LatLng(newBottomOfBound, leftOfBound),
            LatLng(newTopOfBound, rightOfBound)
        )

        mapManager.moveCamera(bound, padding)
    }

    private fun initBottomSheetWithNavigation() {
        binding.bnv.setupWithNavController(navController)
        bottomSheetBehavior.halfExpandedRatio = Constants.BOTTOM_SHEET_HALF_EXPANDED_RATIO
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        binding.sb.doOnLayout {
            bottomSheetBehavior.expandedOffset = binding.sb.height + binding.sb.marginTop * 2
            binding.bs.setPadding(0,0,0,binding.sb.height + binding.sb.marginTop * 2)
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

    private fun halfOpenBottomSheetWhenCollapsed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun openPreviewFragment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        navController.navigate(R.id.previewFragment)
    }

    private fun navigateAuthActivity() {
        startActivity(
            Intent(this, AuthActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
            }
        )
        finish()
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

            sv.editText.setOnEditorActionListener { v, _, _ ->
                getAddressAutoCompletion(v.text.toString())
                true
            }

            sv.addTransitionListener { _, _, afterState ->
                if (afterState == SearchView.TransitionState.HIDDEN) {
                    viewModel.updateAutoCompleteTexts(emptyList())
                }
            }

            btnSearchHere.setOnClickListener {
                mapManager.searchSnapPoints()
            }
        }
    }

    private fun moveCameraToAddress(address: String) {

        with(binding) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(address, 1) { results ->
                    if (results.size == 0) {
                        runOnUiThread { showToastMessage(R.string.search_location_fail) }
                    } else {
                        runOnUiThread {
                            mapManager.moveCamera(results[0].latitude, results[0].longitude)
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            sv.hide()
                        }
                    }
                }
            } else {
                // TODO - runBlocking 대체
                val results =
                    runBlocking(Dispatchers.IO) { geocoder.getFromLocationName(address, 1) }

                if (results == null || results.size == 0) {
                    showToastMessage(R.string.search_location_fail)
                } else {
                    mapManager.moveCamera(results[0].latitude, results[0].longitude)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    sv.hide()
                }
            }
        }
    }

    private fun getAddressAutoCompletion(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                viewModel.updateAutoCompleteTexts(response.autocompletePredictions.map {
                    it.getFullText(null).toString()
                })
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e("TAG", "Place not found: ${exception.statusCode}")
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun checkPermissionAndMoveCameraToUserLocation() {
        if(this.isMyLocationGranted()){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location ?: return@addOnSuccessListener
                    mapManager.moveCamera(latitude = location.latitude, longitude = location.longitude, zoom = 17.5f)

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
            showToastMessage(R.string.activity_main_permission_allow)
        } else {
            showToastMessage(R.string.activity_main_permission_deny)
        }
    }
}