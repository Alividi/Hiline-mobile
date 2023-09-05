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

class LayananRayaEditActivity : AppCompatActivity() {

    private lateinit var mapSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var Alamat: String
    private var marker: Marker? = null
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var ivLayananRaya: ImageView
    private lateinit var prefManager: PrefManager
    private lateinit var etNamaLayanan: EditText
    private lateinit var etKota: EditText
    private lateinit var etProvinsi: EditText
    private lateinit var etTelp: EditText
    private lateinit var btnSimpan: AppCompatButton

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
        setContentView(R.layout.activity_layanan_raya_edit)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnPilihLokasi:AppCompatButton = findViewById(R.id.btnPilihLokasi)
        val btnUbahImg: AppCompatButton = findViewById(R.id.btnUbahImg)
        val mapView: MapView = findViewById(R.id.map)
        val tvAlamat: TextView = findViewById(R.id.tvAlamat)
        btnSimpan = findViewById(R.id.btnSimpan)
        etNamaLayanan = findViewById(R.id.etNamaLayanan)
        etKota = findViewById(R.id.etKota)
        etProvinsi = findViewById(R.id.etProvinsi)
        etTelp = findViewById(R.id.etTelp)
        ivLayananRaya = findViewById(R.id.ivLayananRaya)
        Alamat = ""
        Latitude = 0.0
        Longitude = 0.0

        updateButtonState()

        etNamaLayanan.addTextChangedListener(textWatcher)
        etKota.addTextChangedListener(textWatcher)
        etProvinsi.addTextChangedListener(textWatcher)
        etTelp.addTextChangedListener(textWatcher)

        val imgUri = intent.getStringExtra("aImage")

        if (!imgUri.isNullOrEmpty()) {
            Picasso.get().invalidate(imgUri)
            Picasso.get().load(imgUri).into(ivLayananRaya)
        } else {
            ivLayananRaya.setImageResource(R.drawable.def_hospital_img)
        }

        Alamat = intent.getStringExtra("alamat").toString()
        Latitude = intent.getDoubleExtra("aLatitude", 0.0)
        Longitude = intent.getDoubleExtra("aLongitude", 0.0)

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.setZoom(20.0)
        mapView.controller.setCenter(GeoPoint(Latitude,Longitude))

        val markerLocation = GeoPoint(Latitude,Longitude)
        marker = Marker(mapView)
        marker?.position = markerLocation
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        tvAlamat.text = Alamat
        val nama: String? = intent.getStringExtra("aNama")
        Log.e("aNama: ",intent.getStringExtra("aNama").toString())
        etNamaLayanan.text = nama?.toEditable() ?: "".toEditable()
        val kota: String? = intent.getStringExtra("kota")
        etKota.text = kota?.toEditable() ?: "".toEditable()
        val provinsi: String? = intent.getStringExtra("provinsi")
        etProvinsi.text = provinsi?.toEditable() ?: "".toEditable()
        val telp: String? = intent.getStringExtra("aTelepon")
        Log.e("Telp: ",intent.getStringExtra("aTelepon").toString())
        etTelp.text = telp?.toEditable() ?: "".toEditable()

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

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


        btnSimpan.setOnClickListener {
            editHospital()
        }

        btnUbahImg.setOnClickListener {
            openGallery()
        }

        btnPilihLokasi.setOnClickListener {
            val intent = Intent(this, MapSelectionActivity::class.java)
            mapSelectionLauncher.launch(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    fun editHospital(){
        val hospitalApi: HospitalApi = Retro().getRetroClientInstance().create(HospitalApi::class.java)
        val id = intent.getStringExtra("aId").toString()
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


        val drawable = ivLayananRaya.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap

        val file = File(applicationContext.cacheDir, "image.png")
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        hospitalApi.putHospital(id ,namePart, cityPart, provincePart, addressPart, telpPart, latPart, lngPart, imagePart, tokenAuth
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

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

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

        btnSimpan.isEnabled = isButtonEnabled
        val drawableRes = if (!isButtonEnabled) R.drawable.btn_disable_bg else R.drawable.btn_succes_bg
        val drawable = resources.getDrawable(drawableRes)
        btnSimpan.setBackgroundDrawable(drawable)
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
        ivLayananRaya.setImageDrawable(drawable)
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        dialog.setTitle("Edit Layanan Berhasil")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Perubahan telah berhasil disimpan"

        dialog.setOnDismissListener {
            val intent = Intent(this@LayananRayaEditActivity, LayananRayaAdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}