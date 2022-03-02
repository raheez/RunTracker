package com.app.runtracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.app.runtracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    //region declartation
    companion object {
        final val PERMISSION_REQUEST_CODE_LOCATION = 1000
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mIsCollectLocation: Boolean = false
    private var mPointsList: ArrayList<LatLng>? = null

    private lateinit var mFusedLocationProvider: FusedLocationProviderClient
    private lateinit var mLocationManager: LocationManager
    private var mLocationRequest = com.google.android.gms.location.LocationRequest()
    //endregion


    //region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initListener()
        mPointsList = ArrayList<LatLng>()
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkIfPermissionGranted()) {

        } else {
            Log.d("permission_denied", "please_grant_the_permission")
            requsetPermission()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mFusedLocationProvider?.removeLocationUpdates(mLocationCallback)
    }

    //endregion


    //region methods

    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                if (mIsCollectLocation) {
                    var mLatLng: LatLng
                    mLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("new_lat", "" + location.latitude)
                    Log.d("new_long", "" + location.longitude)
                    mPointsList?.add(mLatLng)
                }
            }
        }
    }

    private fun initListener() {

        binding?.startButton?.setOnClickListener {
            resetData()
            getLocation()
            binding?.stopButton?.visibility = View.VISIBLE
        }

        binding?.stopButton?.setOnClickListener {
            mIsCollectLocation = false
            mMap?.apply {
                this.clear()
                this.addPolyline(
                    PolylineOptions().addAll(mPointsList!!).width(13f).color(Color.BLUE)
                        .geodesic(true)
                )
                this.moveCamera(CameraUpdateFactory.newLatLngZoom(mPointsList?.last()!!, 15f))
            }
            mFusedLocationProvider?.removeLocationUpdates(mLocationCallback)
            binding?.stopButton?.visibility = View.GONE
        }
    }

    private fun resetData() {
        mIsCollectLocation = true
        mMap.clear()
        mPointsList?.clear()
    }

    private fun getLocation() {
        if (checkIfPermissionGranted()) {
            if (isLocationEnabled(mLocationManager)) {
                Log.d("location_enabled", "location_enabled")
                requestNewLocations()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocations() {
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(50);
        mLocationRequest.setSmallestDisplacement(10f)

        //mLocationRequest.setNumUpdates(10);
        mFusedLocationProvider.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!
        )


    }

    private fun checkIfPermissionGranted(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requsetPermission() {
        val mList = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        ActivityCompat.requestPermissions(this, mList, PERMISSION_REQUEST_CODE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {

            var mLocationPermissionGranted = false
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission_result", "coarse_location_permission_granted")
                mLocationPermissionGranted = true
            } else {
                mLocationPermissionGranted = false
            }

            getLocation()

        }
    }

    //endregion


    //region interfaces
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mFusedLocationProvider?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                val mCurrentLocation = LatLng(it.latitude, it.longitude)
                mPointsList?.add(mCurrentLocation)
                mMap?.addMarker(MarkerOptions().position(mCurrentLocation).title("you are here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15f))
            } else {
                // Add a marker in Sydney and move the camera
                val sydney = LatLng(25.20, 55.270)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in dubai"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13f))
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        if (mIsCollectLocation) {
            val latLng: LatLng
            latLng = LatLng(location.latitude, location.longitude)
            mPointsList?.add(latLng)

            Log.d("location_changed_lat", "" + location.latitude)
            Log.d("location_changed_long", "" + location.longitude)
        }
    }
    //endregion

}