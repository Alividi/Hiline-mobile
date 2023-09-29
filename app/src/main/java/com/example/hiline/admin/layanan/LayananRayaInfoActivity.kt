package com.example.hiline.admin.layanan

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.service.HospitalService
import com.example.hiline.response.HospitalResponse
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LayananRayaInfoActivity : AppCompatActivity() {

    private var marker: Marker? = null
    private lateinit var prefManager: PrefManager
    private lateinit var selectedSerial: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_raya_info)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnEdit: AppCompatButton = findViewById(R.id.btnEdit)
        val etNamaLayanan: TextView = findViewById(R.id.etNamaLayanan)
        val etKota: TextView = findViewById(R.id.etKota)
        val etProvinsi: TextView = findViewById(R.id.etProvinsi)
        val etTelp: TextView = findViewById(R.id.etTelp)
        val tvAlamat: TextView = findViewById(R.id.tvAlamat)
        val mapView: MapView = findViewById(R.id.map)
        val btnhapus: TextView = findViewById(R.id.btnhapus)
        val ivLayananRaya: ImageView = findViewById(R.id.ivLayananRaya)

        selectedSerial = intent.getStringExtra("selected_serial") ?: ""
        val Latitude: Double = intent.getDoubleExtra("latitude", 0.0)
        val Longitude: Double = intent.getDoubleExtra("longitude", 0.0)
        val id: String = intent.getStringExtra("id").toString()

        val imgUri = intent.getStringExtra("image")

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(ivLayananRaya)
        } else {
            ivLayananRaya.setImageResource(R.drawable.def_hospital_img)
        }

        etNamaLayanan.text = intent.getStringExtra("nama")
        etKota.text = intent.getStringExtra("kota")
        etProvinsi.text = intent.getStringExtra("provinsi")
        tvAlamat.text =  intent.getStringExtra("alamat")
        etTelp.text = intent.getStringExtra("telepon")

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.setZoom(20.0)
        mapView.controller.setCenter(
            GeoPoint(intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0))
        )

        val markerLocation = GeoPoint(intent.getDoubleExtra("latitude", 0.0),
            intent.getDoubleExtra("longitude", 0.0))
        marker = Marker(mapView)
        marker?.position = markerLocation
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, LayananRayaEditActivity::class.java)
            intent.putExtra("aId", id)
            intent.putExtra("aNama", etNamaLayanan.text.toString())
            Log.e("aNama",etNamaLayanan.text.toString())
            intent.putExtra("kota", etKota.text)
            intent.putExtra("provinsi", etProvinsi.text)
            intent.putExtra("alamat", tvAlamat.text)
            intent.putExtra("aTelepon", etTelp.text.toString())
            Log.e("aTelepon: ", etTelp.text.toString())
            intent.putExtra("aLatitude", Latitude)
            intent.putExtra("aLongitude", Longitude)
            intent.putExtra("aImage", imgUri)
            intent.putExtra("selected_serial", selectedSerial)
            Log.e("serial: ", selectedSerial)
            startActivity(intent)
        }

        btnhapus.setOnClickListener {
            showDeleteDialog()
        }
    }

    fun deleteHospital(){
        val hospitalId = intent.getStringExtra("id").toString()
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroHospitalUrl(okHttpClient)
        val service = retrofit.create(HospitalService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.deleteHospital(hospitalId,aToken)
        call.enqueue(object : Callback<HospitalResponse> {
            override fun onResponse(call: Call<HospitalResponse>, response: Response<HospitalResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }

            override fun onFailure(call: Call<HospitalResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val tvTitle: TextView = dialog.findViewById(R.id.tvTitle)
        val tvKet: TextView = dialog.findViewById(R.id.tvKet)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        tvTitle.text = "Hapus Layanan?"
        tvKet.text = "Aksi ini tidak dapat dibatalkan dan akan dihilangkan dari Layanan Raya"
        dialog.setTitle("Hapus Layanan")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        btnHapus.setOnClickListener {
            deleteHospital()
            val intent = Intent(this@LayananRayaInfoActivity, LayananRayaAdminActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }
}