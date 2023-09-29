package com.example.hiline.admin.layanan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hiline.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.properties.Delegates

class MapSelectionActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var marker: Marker? = null
    private var geocoder: Geocoder? = null
    private lateinit var addressText: String
    private lateinit var placeEditText: EditText

    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))
        geocoder = Geocoder(this)

        setContentView(R.layout.activity_map_selection)
        placeEditText = findViewById(R.id.placeEditText)

        val findButton: Button = findViewById(R.id.findButton)
        findButton.setOnClickListener {
            val placeName = placeEditText.text.toString()
            if (placeName.isNotEmpty()) {
                findLocationFromPlaceName(placeName)
            } else {
                Toast.makeText(this, "Isi nama tempat",Toast.LENGTH_SHORT).show()
            }
        }

        Latitude = 0.0
        Longitude = 0.0

        mapView = findViewById(R.id.mapView)
        mapView.controller.setZoom(17.0)
        mapView.setMultiTouchControls(true)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Longitude = location.longitude
                Latitude = location.latitude
                Log.e("latitut , longitut:", Latitude.toString()+","+Longitude.toString())
                mapView.controller.setCenter(GeoPoint(Latitude,Longitude))
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        mapView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                val clickedGeoPoint = mapView.projection.fromPixels(x, y)

                marker?.let {
                    mapView.overlays.remove(it)
                    marker = null
                }
                marker = Marker(mapView)
                marker?.position = clickedGeoPoint as GeoPoint?
                mapView.overlays.add(marker)
                mapView.invalidate()

                marker?.let {
                    it.icon = ContextCompat.getDrawable(this, R.drawable.ic_marker_default)
                    it.title = "Clicked Location"
                    it.snippet = "Latitude: ${clickedGeoPoint.latitude}, Longitude: ${clickedGeoPoint.longitude}"
                }
                val addresses: List<Address> = geocoder?.getFromLocation(clickedGeoPoint.latitude, clickedGeoPoint.longitude, 1) ?: emptyList()
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    addressText = address.getAddressLine(0)
                }
            }
            false
        }

        val confirmButton: Button = findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val latitude = marker?.position?.latitude ?: 0.0
            val longitude = marker?.position?.longitude ?: 0.0

            val intent = Intent()
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("address", addressText)
            setResult(RESULT_OK, intent)
            finish()
        }

        val locationOverlay = MyLocationNewOverlay(mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BETWEEN_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            locationListener
        )
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val MIN_TIME_BETWEEN_UPDATES = 1000L
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1.0f
    }

    private fun findLocationFromPlaceName(placeName: String) {
        val addresses: List<Address> = geocoder?.getFromLocationName(placeName, 5) ?: emptyList()
        if (addresses.isNotEmpty()) {
            val addressOptions = addresses.map { address ->
                "${address.featureName}, ${address.thoroughfare}, ${address.locality}"
            }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Select Address")
                .setItems(addressOptions) { dialog, selectedIndex ->
                    val selectedAddress = addresses[selectedIndex]
                    val latitude = selectedAddress.latitude
                    val longitude = selectedAddress.longitude

                    mapView.controller.setCenter(GeoPoint(latitude, longitude))

                    marker?.let {
                        mapView.overlays.remove(it)
                        marker = null
                    }
                    marker = Marker(mapView)
                    marker?.position = GeoPoint(latitude, longitude)
                    mapView.overlays.add(marker)
                    mapView.invalidate()

                    addressText = selectedAddress.getAddressLine(0)

                    placeEditText.setText("")
                    dialog.dismiss()
                }
                .show()
            Toast.makeText(this, "Pilih salah satu", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Tempat gk ada", Toast.LENGTH_SHORT).show()
        }
    }
}
