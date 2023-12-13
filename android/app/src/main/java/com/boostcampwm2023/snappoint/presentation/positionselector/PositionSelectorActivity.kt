package com.boostcampwm2023.snappoint.presentation.positionselector

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMapsMarkerBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.util.Constants
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class PositionSelectorActivity : BaseActivity<ActivityMapsMarkerBinding>(R.layout.activity_maps_marker),
    OnMapReadyCallback
{
    private val viewModel: PositionSelectorViewModel by viewModels()
    private var startLatLng = LatLng(37.3586926, 127.1051209)
    private var index: Int = 0
    private val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private lateinit var placesClient: PlacesClient
    private val geocoder: Geocoder by lazy { Geocoder(applicationContext) }

    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtra()
        initPlacesClient()
        initBinding()
        collectViewModelData()

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_google_map) as SupportMapFragment
        map.getMapAsync(this)

        binding.btnConfirmPosition.setOnClickListener {
            setIntentExtra()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initPlacesClient() {
        Places.initializeWithNewPlacesApiEnabled(applicationContext, Constants.API_KEY)
        placesClient = Places.createClient(this)
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel

            sv.editText.setOnEditorActionListener { v, _, _ ->
                getAddressAutoCompletion(v.text.toString())
                hideKeyboard()
                true
            }

            sv.addTransitionListener { _, _, afterState ->
                if (afterState == SearchView.TransitionState.HIDDEN) {
                    viewModel.updateAutoCompleteTexts(emptyList())
                }
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is PositionSelectorEvent.MoveCameraToAddress -> {
                            val address = viewModel.searchViewUiState.value.texts[event.index]
                            moveCameraToAddress(address)
                        }
                    }
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

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun moveCameraToAddress(address: String) {

        with(binding) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(address, 1) { results ->
                    if (results.size == 0) {
                        runOnUiThread { showToastMessage(R.string.search_location_fail) }
                    } else {
                        runOnUiThread {
                            _map?.moveCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(results[0].latitude, results[0].longitude)
                                )
                            )
                            sv.hide()
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    val results =
                        withContext(Dispatchers.IO) { geocoder.getFromLocationName(address, 1) }

                    if (results == null || results.size == 0) {
                        showToastMessage(R.string.search_location_fail)
                    } else {
                        _map?.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                LatLng(results[0].latitude, results[0].longitude)
                            )
                        )
                        sv.hide()
                    }
                }
            }
        }
    }

    private fun setIntentExtra() {
        intent.putExtra("address", getAddressLine())
        intent.putExtra("index", index)
        intent.putExtra("latitude", _map?.cameraPosition?.target?.latitude)
        intent.putExtra("longitude", _map?.cameraPosition?.target?.longitude)
    }

    private fun getIntentExtra() {
        intent.getDoubleArrayExtra("position")?.let{
            startLatLng = LatLng(it[0], it[1])
        }
        index = intent.getIntExtra("index",0)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _map = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17.5f))
    }

    override fun onDestroy() {
        super.onDestroy()

        _map = null
    }

    private fun getAddressLine(): String {
        val geocoder = Geocoder(applicationContext, Locale.KOREA)
        val map: GoogleMap = _map ?: return ""
        val position: LatLng = map.cameraPosition.target

        val address = runBlocking { geocoder.getFromLocation(position.latitude, position.longitude, 1) }
        if (address.isNullOrEmpty()) return ""
        return address[0].getAddressLine(0)
    }
}