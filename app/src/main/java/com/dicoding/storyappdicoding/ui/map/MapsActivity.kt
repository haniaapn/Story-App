package com.dicoding.storyappdicoding.ui.map

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.storyappdicoding.R
import com.dicoding.storyappdicoding.databinding.ActivityMapsBinding
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dicoding.storyappdicoding.data.remote.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.user_location)

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: MapsViewModel by viewModels { factory }
        mapsViewModel = viewModel

        binding.sfMap.setOnRefreshListener { viewModel.getLocation() }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mapsViewModel.getLocation()
        getMyLocation()
        observeAddManyMarker(mMap)
    }

    private fun observeAddManyMarker(mMap: GoogleMap) {
        mapsViewModel.locationResult.observe(this){ result ->
            when (result) {
                is Result.Loading -> showRefresh(true)
                is Result.Success -> {
                    showRefresh(false)
                    if (result.data.isNotEmpty()){
                        for (stories in result.data){
                            val latLng = stories.lat?.let { stories.lon?.let { it1 -> LatLng(it, it1) } }
                            val marker = latLng?.let { MarkerOptions().position(it).title(stories.name).snippet(stories.description)
                            }?.let { mMap.addMarker(it) }
                            marker?.tag = stories.id
                            if (latLng != null){
                                boundsBuilder.include(latLng)
                            }
                        }
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                is Result.Error -> {
                    showRefresh(false)
                    handleLoginError(result.error)
                }
            }

        }
    }

    private fun handleLoginError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        Log.e("ERRORNYA ->", error)
    }

    private fun showRefresh(isRefresh: Boolean) {
        binding.sfMap.isRefreshing = isRefresh
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}