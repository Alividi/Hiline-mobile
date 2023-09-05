package com.example.hiline.admin

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.HospitalApi
import com.example.hiline.model.HospitalResponse
import com.example.hiline.model.HospitalsResponse
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

class LayananRayaTambahActivity : AppCompatActivity() {

    private lateinit var mapSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var Alamat: String
    private var marker: Marker? = null
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var btnTambahImg: ImageView
    private lateinit var prefManager: PrefManager
    private lateinit var etNamaLayanan: EditText
    private lateinit var etKota: EditText
    private lateinit var etProvinsi: EditText
    private lateinit var etTelp: EditText
    private lateinit var btnTambah: AppCompatButton

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    loadImage(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_raya_tambah)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnPilihLokasi:AppCompatButton = findViewById(R.id.btnPilihLokasi)
        val mapView: MapView = findViewById(R.id.map)
        val tvAlamat: TextView = findViewById(R.id.tvAlamat)
        btnTambah = findViewById(R.id.btnTambah)
        etNamaLayanan = findViewById(R.id.etNamaLayanan)
        etKota = findViewById(R.id.etKota)
        etProvinsi = findViewById(R.id.etProvinsi)
        etTelp = findViewById(R.id.etTelp)
        btnTambahImg = findViewById(R.id.btnTambahImg)
        Alamat = ""
        Latitude = 0.0
        Longitude = 0.0

        updateButtonState()

        etNamaLayanan.addTextChangedListener(textWatcher)
        etKota.addTextChangedListener(textWatcher)
        etProvinsi.addTextChangedListener(textWatcher)
        etTelp.addTextChangedListener(textWatcher)

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        btnTambah.setOnClickListener {
            inputHospital()
        }

        mapSelectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val latitude = data?.getDoubleExtra("latitude", 0.0)
                val longitude = data?.getDoubleExtra("longitude", 0.0)
                val alamat = data?.getStringExtra("address")

                if (latitude != null) {
                    Latitude = latitude
                }
                if (longitude != null) {
                    Longitude = longitude
                }
                if (alamat != null) {
                    Alamat = alamat
                }

                mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                mapView.controller.setZoom(20.0)
                mapView.controller.setCenter(GeoPoint(Latitude,Longitude))

                val markerLocation = GeoPoint(Latitude,Longitude)

                marker?.let {
                    mapView.overlays.remove(it)
                    marker = null
                }

                marker = Marker(mapView)
                marker?.position = markerLocation
                marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)

                tvAlamat.text = Alamat
            }
        }

        btnTambahImg.setOnClickListener {
            openGallery()
        }

        btnPilihLokasi.setOnClickListener {
            val intent = Intent(this, MapSelectionActivity::class.java)
            mapSelectionLauncher.launch(intent)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, LayananRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        val name = etNamaLayanan.text.toString().trim()
        val city = etKota.text.toString().trim()
        val province = etProvinsi.text.toString().trim()
        val telp = etTelp.text.toString().trim()

        val isButtonEnabled =
            name.isNotEmpty() &&
                    city.isNotEmpty() &&
                    province.isNotEmpty() &&
                    telp.isNotEmpty()

        btnTambah.isEnabled = isButtonEnabled
        val drawableRes = if (!isButtonEnabled) R.drawable.btn_disable_bg else R.drawable.btn_apricot_bg
        val drawable = resources.getDrawable(drawableRes)
        btnTambah.setBackgroundDrawable(drawable)
    }

    fun inputHospital(){
        val hospitalApi: HospitalApi = Retro().getRetroClientInstance().create(HospitalApi::class.java)
        val name = etNamaLayanan.text.toString()
        val city = etKota.text.toString()
        val province = etProvinsi.text.toString()
        val address = Alamat
        val telp = etTelp.text.toString()
        val lat = Latitude
        val lng = Longitude
        val tokenAuth = "Bearer ${prefManager.getToken()}"

        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val cityPart = city.toRequestBody("text/plain".toMediaTypeOrNull())
        val provincePart = province.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val telpPart = telp.toRequestBody("text/plain".toMediaTypeOrNull())
        val latPart = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lngPart = lng.toString().toRequestBody("text/plain".toMediaTypeOrNull())


        val drawable = btnTambahImg.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap

        val file = File(applicationContext.cacheDir, "image.png")
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        hospitalApi.postHospital(namePart, cityPart, provincePart, addressPart, telpPart, latPart, lngPart, imagePart, tokenAuth
        ).enqueue(object : Callback<HospitalResponse> {
                override fun onResponse(call: Call<HospitalResponse>, response: Response<HospitalResponse>) {
                    if (response.isSuccessful) {
                        val hospitalResponse = response.body()
                        if (hospitalResponse != null) {
                            val rawResponse = response.raw().toString()
                            Log.e("Raw Response: ", rawResponse)
                            showDialog()
                        } else {
                            Log.e("Response Body", "HospitalResponse is null")
                        }
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        val errorCode = response.code()
                        val errorMessage = response.message()
                        Log.e("Unseccessful: ", errorCode.toString() + " " + errorMessage)
                    }
                }

                override fun onFailure(call: Call<HospitalResponse>, t: Throwable) {
                    Log.e("API Call", "Failed: " + t.toString())
                }
            })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImage(imageUri: Uri) {
        val drawable = try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            BitmapDrawable(resources, bitmap)
        } catch (e: Exception) {
            val drawable = Drawable.createFromStream(contentResolver.openInputStream(imageUri), imageUri.toString())
            drawable
        }
        btnTambahImg.setImageDrawable(drawable)
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        dialog.setTitle("Tambah Layanan Berhasil")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Layanan berhasil diunggah"

        dialog.setOnDismissListener {
            val intent = Intent(this@LayananRayaTambahActivity, LayananRayaAdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this, LayananRayaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}