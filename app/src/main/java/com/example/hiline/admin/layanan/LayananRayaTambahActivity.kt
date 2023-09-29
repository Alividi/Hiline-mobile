package com.example.hiline.admin.layanan

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
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.model.HospitalModel
import com.example.hiline.request.HospitalRequest
import com.example.hiline.response.CityResponse
import com.example.hiline.service.Retro
import com.example.hiline.service.HospitalService
import com.example.hiline.response.HospitalResponse
import com.example.hiline.response.HospitalsResponse
import com.example.hiline.response.ProvinceResponse
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.service.WilayahService
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

class LayananRayaTambahActivity : AppCompatActivity() {

    data class Province(
        val serial: String?,
        val nama: String?
    )
    private val provinceDataList = mutableListOf<Province>()
    private lateinit var mapSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var Alamat: String
    private var marker: Marker? = null
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var btnTambahImg: ImageView
    private lateinit var prefManager: PrefManager
    private lateinit var etNamaLayanan: EditText
    private lateinit var etTelp: EditText
    private lateinit var btnTambah: AppCompatButton
    private lateinit var sProvince: Spinner
    private lateinit var sCity: Spinner
    private lateinit var sProvinceAdapter: ArrayAdapter<String>
    private val provinceNamesList = mutableListOf<String>()
    private lateinit var sCityAdapter: ArrayAdapter<String>
    private val cityNamesList = mutableListOf<String>()
    private var pSerial: String = ""
    private var cSerial: String = ""
    private var base64Img: String = ""

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
        etTelp = findViewById(R.id.etTelp)
        btnTambahImg = findViewById(R.id.btnTambahImg)
        sProvince = findViewById(R.id.sProvinsi)
        sCity = findViewById(R.id.sKota)
        Alamat = ""
        Latitude = 0.0
        Longitude = 0.0

        sCityAdapter = ArrayAdapter(
            this@LayananRayaTambahActivity,
            android.R.layout.simple_spinner_item,
            cityNamesList
        )
        sCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sCity.adapter = sCityAdapter

        updateButtonState()
        getProvince()

        etNamaLayanan.addTextChangedListener(textWatcher)
        etTelp.addTextChangedListener(textWatcher)

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        sProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                pSerial = provinceDataList[position].serial ?: ""
                sCityAdapter.clear()
                getCity()
                sCityAdapter.notifyDataSetChanged()
                Log.e("pSerial: ",pSerial)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }
        }

        btnTambah.setOnClickListener {
            createHospital()
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
        val telp = etTelp.text.toString().trim()

        val isButtonEnabled = name.isNotEmpty() && telp.isNotEmpty()

        btnTambah.isEnabled = isButtonEnabled
        val drawableRes = if (!isButtonEnabled) R.drawable.btn_disable_bg else R.drawable.btn_apricot_bg
        val drawable = resources.getDrawable(drawableRes)
        btnTambah.setBackgroundDrawable(drawable)
    }

    fun getProvince(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroWilayahUrl(okHttpClient)
        val service = retrofit.create(WilayahService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getProvince(aToken)
        call.enqueue(object : Callback<ProvinceResponse> {
            override fun onResponse(call: Call<ProvinceResponse>, response: Response<ProvinceResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val provinceResponse = response.body()
                    provinceResponse?.data?.let { provinceDataList ->
                        for (provinceData in provinceDataList) {
                            provinceData.nama?.let {
                                provinceNamesList.add(it)
                            }
                        }
                        sProvinceAdapter = ArrayAdapter(
                            this@LayananRayaTambahActivity,
                            android.R.layout.simple_spinner_item,
                            provinceNamesList
                        )
                        sProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sProvince.adapter = sProvinceAdapter
                        sProvinceAdapter.notifyDataSetChanged()
                    }
                    provinceResponse?.data?.let { provinceDataListResponse ->
                        for (provinceDataResponse in provinceDataListResponse) {
                            val province = Province(
                                serial = provinceDataResponse.serial,
                                nama = provinceDataResponse.nama
                            )
                            provinceDataList.add(province)
                        }
                    }

                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }

            override fun onFailure(call: Call<ProvinceResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun getCity(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroWilayahUrl(okHttpClient)
        val service = retrofit.create(WilayahService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getCity(pSerial,aToken)
        call.enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val cityResponse = response.body()
                    cityResponse?.data?.let { cityDataList ->
                        for (cityData in cityDataList) {
                            cityData.nama?.let {
                                cityNamesList.add(it)
                            }
                        }
                        sCityAdapter = ArrayAdapter(
                            this@LayananRayaTambahActivity,
                            android.R.layout.simple_spinner_item,
                            cityNamesList
                        )
                        sCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sCity.adapter = sCityAdapter
                        sCityAdapter.notifyDataSetChanged()
                    }

                    sCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                            cSerial = cityResponse?.data?.get(position)?.serial ?: ""
                            Log.e("Serial: ",cSerial)
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>) {

                        }
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun createHospital(){
        val request = HospitalRequest()
        request.name = etNamaLayanan.text.toString().trim()
        request.address = Alamat
        request.phone = etTelp.text.toString().trim()
        request.wilayah_serial = cSerial
        request.latitude = Latitude
        request.longitude = Longitude
        request.image = "data:image/png;base64,$base64Img"

        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroHospitalUrl(okHttpClient)
        val service = retrofit.create(HospitalService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.createHospital(request,aToken)
        Log.e("Request:", call.request().toString())
        call.enqueue(object : Callback<HospitalResponse> {
            override fun onResponse(call: Call<HospitalResponse>, response: Response<HospitalResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Serial: ",cSerial)
                    showDialog()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImage(imageUri: Uri) {
        val bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            val base64Image = bitmapToBase64(bitmap)
            base64Img = base64Image
            //Log.e("Base64: ",base64Img)
            btnTambahImg.setImageBitmap(bitmap)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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