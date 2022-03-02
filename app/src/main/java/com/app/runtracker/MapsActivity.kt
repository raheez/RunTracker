package com.app.runtracker

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions
import java.util.jar.Manifest

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
        val mPoint1 = LatLng(21.00, 51.00)
        val mPoint2 = LatLng(31.00, 61.00)
        val mPoint3 = LatLng(41.00, 71.00)
        val mPoint4 = LatLng(51.00, 81.00)

        mPointsList = ArrayList<LatLng>()
        mPointsList?.add(mPoint1)
        mPointsList?.add(mPoint2)
        mPointsList?.add(mPoint3)
        mPointsList?.add(mPoint4)


        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLastLocation()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //endregion


    //region methods
    private fun initListener() {
        binding?.startButton?.setOnClickListener {
            mIsCollectLocation = true
            Toast.makeText(this, "started", Toast.LENGTH_SHORT).show()
        }
        binding?.stopButton?.setOnClickListener {
            mIsCollectLocation = false
            mMap.clear()
            mMap.addPolyline(
                PolylineOptions().addAll(mPointsList!!).width(15f).color(Color.BLUE).geodesic(true)
            )
            Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLastLocation() {
        if (checkIfPermissionGranted()) {

            if (isLocationEnabled(mLocationManager)) {
                Log.d("permission_result","location_enabled")
            }

        } else {
            Log.d("permission_denied","please_grant_the_permission")
            requsetPermission()
        }
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

        if(requestCode == PERMISSION_REQUEST_CODE_LOCATION){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("permission_result","coarse_location_permission_granted")
            }

            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.d("permission_result","fine_location_permission_granted")
            }
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addPolyline(
            PolylineOptions().addAll(mPointsList!!).width(15f).color(Color.RED).geodesic(true)
        )

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(25.20, 55.270)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13f))
    }

    override fun onLocationChanged(location: Location) {
        if (mIsCollectLocation) {
            val latLng: LatLng
            latLng = LatLng(location.latitude, location.longitude)
            mPointsList?.add(latLng)
        }
    }
    //endregion
}