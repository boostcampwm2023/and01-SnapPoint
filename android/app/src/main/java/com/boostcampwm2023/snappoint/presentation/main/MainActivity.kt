package com.boostcampwm2023.snappoint.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityMainBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback
{
    private val viewModel: MainViewModel by viewModels()
    private var _map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.bs) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bnv.setupWithNavController(navController)

        val map: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fcv_main_map) as SupportMapFragment
        map.getMapAsync(this)

        initBinding()



        binding.sb.setOnClickListener {
            binding.sv.show()
        }
  /*      binding.sb.setNavigationOnClickListener {
            Log.d("TAG", "onCreate: navigation clicked")
        }*/
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