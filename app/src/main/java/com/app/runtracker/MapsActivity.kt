package com.app.runtracker

import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.app.runtracker.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mIsCollectLocation: Boolean = false
    private var mPointsList : ArrayList<LatLng>?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initListener()
        val mPoint1 = LatLng(21.00,51.00)
        val mPoint2 = LatLng(31.00,61.00)
        val mPoint3 = LatLng(41.00,71.00)
        val mPoint4 = LatLng(51.00,81.00)

        mPointsList = ArrayList<LatLng>()
        mPointsList?.add(mPoint1)
        mPointsList?.add(mPoint2)
        mPointsList?.add(mPoint3)
        mPointsList?.add(mPoint4)

    }

    private fun initListener() {

        binding?.startButton?.setOnClickListener {
            mIsCollectLocation = true
            Toast.makeText(this,"started",Toast.LENGTH_SHORT).show()
        }

        binding?.stopButton?.setOnClickListener {


            mIsCollectLocation = false
            mMap.clear()
            mMap.addPolyline(PolylineOptions().addAll(mPointsList!!).width(15f).color(Color.BLUE).geodesic(true))
            Toast.makeText(this,"stoped",Toast.LENGTH_SHORT).show()

        }


    }

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

        mMap.addPolyline(PolylineOptions().addAll(mPointsList!!).width(15f).color(Color.RED).geodesic(true))

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(25.20, 55.270)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13f))
    }

    override fun onLocationChanged(location: Location) {
        if (mIsCollectLocation){
            val latLng : LatLng
            latLng = LatLng(location.latitude,location.longitude)
            mPointsList?.add(latLng)
        }
    }
}