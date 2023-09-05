package com.example.hiline.user

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.TypefaceSpan
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LayananRayaDetailActivity : AppCompatActivity() {

    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_raya_detail)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnBukaMap: AppCompatButton = findViewById(R.id.btnBukaMap)
        val tvNamaLayanan: TextView = findViewById(R.id.tvNamaLayanan)
        val tvAlamatLayanan: TextView = findViewById(R.id.tvAlamatLayanan)
        val tvNoTelp: TextView = findViewById(R.id.tvNoTelp)
        val tvJarak: TextView = findViewById(R.id.tvJarak)
        val mapView: MapView = findViewById(R.id.map)

        tvNamaLayanan.text = intent.getStringExtra("nama")
        tvAlamatLayanan.text = "Alamat: " + intent.getStringExtra("alamat")
        tvNoTelp.text = intent.getStringExtra("telepon")
        tvJarak.text = intent.getDoubleExtra("jarak", 0.0).toString() + " Km"

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.setZoom(20.0)
        mapView.controller.setCenter(
            GeoPoint(intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0)))

        val markerLocation = GeoPoint(intent.getDoubleExtra("latitude", 0.0),
            intent.getDoubleExtra("longitude", 0.0))
        marker = Marker(mapView)
        marker?.position = markerLocation
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        btnBack.setOnClickListener {
            finish()
        }

        btnBukaMap.setOnClickListener {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)

            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            val mapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            val mapWebIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(mapWebIntent)
            }
        }
    }

    override fun onBackPressed() {

    }
}