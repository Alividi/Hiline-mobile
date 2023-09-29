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
import com.example.hiline.request.HospitalRequest
import com.example.hiline.response.CityResponse
import com.example.hiline.service.Retro
import com.example.hiline.service.HospitalService
import com.example.hiline.response.HospitalResponse
import com.example.hiline.response.ProvinceResponse
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.service.WilayahService
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
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

class LayananRayaEditActivity : AppCompatActivity() {
    data class Province(
        val serial: String?,
        val nama: String?
    )
    data class City(
        val serial: String?,
        val nama: String?
    )
    private val provinceDataList = mutableListOf<Province>()
    private val cityDataList = mutableListOf<City>()
    private lateinit var mapSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var Alamat: String
    private var marker: Marker? = null
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var ivLayananRaya: ImageView
    private lateinit var prefManager: PrefManager
    private lateinit var etNamaLayanan: EditText
    private lateinit var etTelp: EditText
    private lateinit var btnSimpan: AppCompatButton
    private lateinit var sProvince: Spinner
    private lateinit var sCity: Spinner
    private lateinit var sProvinceAdapter: ArrayAdapter<String>
    private val provinceNamesList = mutableListOf<String>()
    private lateinit var sCityAdapter: ArrayAdapter<String>
    private val cityNamesList = mutableListOf<String>()
    private var pSerial: String = ""
    private var cSerial: String = ""
    private var base64Img: String = ""
    private lateinit var selectedSerial: String

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
        etTelp = findViewById(R.id.etTelp)
        sProvince = findViewById(R.id.sProvinsi)
        sCity = findViewById(R.id.sKota)
        ivLayananRaya = findViewById(R.id.ivLayananRaya)
        selectedSerial = intent.getStringExtra("selected_serial") ?: ""
        Log.e("edit serial", selectedSerial)
        Alamat = ""
        Latitude = 0.0
        Longitude = 0.0

        sCityAdapter = ArrayAdapter(
            this@LayananRayaEditActivity,
            android.R.layout.simple_spinner_item,
            cityNamesList
        )
        sCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sCity.adapter = sCityAdapter

        updateButtonState()
        getProvince()

        etNamaLayanan.addTextChangedListener(textWatcher)
        etTelp.addTextChangedListener(textWatcher)

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

        if (selectedSerial != null && selectedSerial.isNotEmpty()) {
            val pSerial = selectedSerial.substring(0, 2)
            Log.e("pSerial selected: ",pSerial)
            val cSerial = selectedSerial
            Log.e("cSerial selected: ",cSerial)

            val provincePosition = provinceDataList.indexOfFirst { it.serial == pSerial }
            val cityPosition = cityDataList.indexOfFirst { it.serial == cSerial }

            if (provincePosition != -1) {
                sProvince.setSelection(provincePosition)
            }

            if (cityPosition != -1) {
                sCity.setSelection(cityPosition)
            }
        }

        val imgUri = intent.getStringExtra("aImage")
        loadImageAwal(imgUri)
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
            updateHospital()
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
                            this@LayananRayaEditActivity,
                            android.R.layout.simple_spinner_item,
                            provinceNamesList
                        )
                        sProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sProvince.adapter = sProvinceAdapter
                        sProvinceAdapter.notifyDataSetChanged()
                    }
                    provinceResponse?.data?.let { provinceDataListResponse ->
                        for (provinceDataResponse in provinceDataListResponse) {
                            val province = LayananRayaEditActivity.Province(
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
                            this@LayananRayaEditActivity,
                            android.R.layout.simple_spinner_item,
                            cityNamesList
                        )
                        sCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sCity.adapter = sCityAdapter
                        sCityAdapter.notifyDataSetChanged()
                    }

                    cityResponse?.data?.let { cityDataListResponse ->
                        for (cityDataResponse in cityDataListResponse) {
                            val province = LayananRayaEditActivity.Province(
                                serial = cityDataResponse.serial,
                                nama = cityDataResponse.nama
                            )
                            provinceDataList.add(province)
                        }
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

    fun updateHospital(){
        val id = intent.getStringExtra("aId").toString()
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
        val call = service.updateHospital(id,request,aToken)
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
        val telp = etTelp.text.toString().trim()

        val isButtonEnabled =
            name.isNotEmpty() && telp.isNotEmpty()

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
        val bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            val base64Image = bitmapToBase64(bitmap)
            base64Img = base64Image
            Log.e("Base64: ",base64Img)
            ivLayananRaya.setImageBitmap(bitmap)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun loadImageAwal(imageUrl: String?) {
        Picasso.get().load(imageUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    val base64Image = bitmapToBase64(bitmap)
                    base64Img = base64Image
                    //Log.e("Base64: ", base64Img)
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("Image Load Error", e?.message ?: "Unknown error")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
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