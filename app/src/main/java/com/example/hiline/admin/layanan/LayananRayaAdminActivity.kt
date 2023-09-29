package com.example.hiline.admin.layanan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.layanan.LayananRayaAdminAdapter
import com.example.hiline.adapter.layanan.LayananRayaUserAdapter
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.HospitalService
import com.example.hiline.interfaces.LayananRayaInterface
import com.example.hiline.model.HospitalModel
import com.example.hiline.response.HospitalsResponse
import com.example.hiline.service.PrefManager
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.layanan.LayananRayaActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.internal.http2.Http2Reader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class LayananRayaAdminActivity : AppCompatActivity(), LayananRayaInterface {

    private lateinit var hospitalModels: ArrayList<HospitalModel>
    private lateinit var rvLayananRaya: RecyclerView
    private lateinit var adapter: LayananRayaAdminAdapter
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var prefManager: PrefManager
    private var totalPage = 1
    private var currentPage = 1
    private val LOAD_MORE_THRESHOLD = 0.9
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private var keyword:String = ""
    private lateinit var svLayananRaya: SearchView
    private val handler = Handler()
    private val apiCallRunnable = Runnable { performApiCall() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_raya_admin)
        prefManager = PrefManager(this)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        val fab: FloatingActionButton = findViewById(R.id.fabTambah)
        svLayananRaya = findViewById(R.id.svLayananRaya)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        fab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        Latitude = DEFAULT_LATITUDE
        Longitude = DEFAULT_LONGITUDE

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        hospitalModels = ArrayList()
        progressBar.visibility = View.VISIBLE
        getHospital(latitude = 0.0, longitude = 0.0, page = 1)
        rvLayananRaya = findViewById(R.id.rvLayananRaya)
        layoutManager = LinearLayoutManager(this@LayananRayaAdminActivity)
        rvLayananRaya.layoutManager = layoutManager
        adapter = LayananRayaAdminAdapter(hospitalModels, this@LayananRayaAdminActivity)
        rvLayananRaya.adapter = adapter

        rvLayananRaya.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                val percentageScrolled =
                    (lastVisibleItemPosition + 1) / totalItemCount.toDouble()

                if (percentageScrolled >= LOAD_MORE_THRESHOLD && currentPage < totalPage) {
                    loadNextPage()
                }
            }
        })

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Longitude = location.longitude
                Latitude = location.latitude
                //if (Latitude != 0.0 && Longitude != 0.0) {

                //}
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        svLayananRaya.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //filterList(newText)
                handler.removeCallbacks(apiCallRunnable)
                handler.postDelayed(apiCallRunnable, 300)
                return true
            }
        })

        btnBack.setOnClickListener {
            val intent = Intent(this, MainAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        fab.setOnClickListener {
            val intent = Intent(this, LayananRayaTambahActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performApiCall() {
        keyword = svLayananRaya.query.toString()
        hospitalModels.clear()
        getHospital(Latitude, Longitude, currentPage)
    }

    override fun onResume() {
        super.onResume()
        //requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun loadNextPage() {
        if (currentPage < totalPage) {
            currentPage++
            getHospital(Latitude, Longitude, currentPage)
        }
    }

    fun getHospital(latitude: Double, longitude: Double, page: Int) {
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroHospitalUrl(okHttpClient)
        val service = retrofit.create(HospitalService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getHospitals(latitude, longitude, 100000000, "", keyword, page, 10, aToken)
        //Log.e("API Request", call.request().toString())
        call.enqueue(object : Callback<HospitalsResponse> {
            override fun onResponse(call: Call<HospitalsResponse>, response: Response<HospitalsResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    val hospitalResponse = response.body()
                    val hospitals = hospitalResponse?.data?.datas
                    totalPage = hospitalResponse?.data?.totalPage ?: 1
                    Log.e("totalpage: ", totalPage.toString())
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("aToken: ", prefManager.getAccessToken().toString())
                    Log.e("rToken: ", prefManager.getRefreshToken().toString())

                    hospitals?.forEach { hospital ->
                        hospitalModels.add(
                            HospitalModel(
                                hospital.id,
                                hospital.name,
                                hospital.wilayah?.city,
                                hospital.wilayah?.province,
                                hospital.address,
                                hospital.phone,
                                hospital.image,
                                hospital.latitude,
                                hospital.longitude,
                                hospital.distance,
                                hospital.wilayah?.serial
                            )
                        )
                    }
                    Log.e("Pagination", "HospitalModels size: ${hospitalModels.size}")
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<HospitalsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
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

        private const val DEFAULT_LATITUDE = 0.0
        private const val DEFAULT_LONGITUDE = 0.0
    }

    override fun onItemClick(position: Int) {
        val rs = hospitalModels[position]
        val intent = Intent(this, LayananRayaInfoActivity::class.java)
        intent.putExtra("id", rs.id)
        intent.putExtra("nama", rs.nama)
        intent.putExtra("kota", rs.kota)
        intent.putExtra("selected_serial", rs.serial)
        intent.putExtra("provinsi", rs.provinsi)
        intent.putExtra("alamat", rs.alamat)
        intent.putExtra("telepon", rs.telepon)
        intent.putExtra("latitude", rs.latitude)
        intent.putExtra("longitude", rs.longitude)
        intent.putExtra("jarak", rs.jarak)
        intent.putExtra("image", rs.image)
        intent.putExtra("aLatitude", Latitude)
        intent.putExtra("aLongitude", Longitude)
        startActivity(intent)
    }

    private fun filterList(text: String) {
        val filterArray = ArrayList<HospitalModel>()
        for (hospitalModel in hospitalModels) {
            if (hospitalModel.nama?.lowercase()?.contains(text.lowercase()) == true ||
                hospitalModel.provinsi?.lowercase()?.contains(text.lowercase()) == true ||
                hospitalModel.kota?.lowercase()?.contains(text.lowercase()) == true
            ) {
                filterArray.add(hospitalModel)
            }
        }
        if (filterArray.isEmpty()) {
            Toast.makeText(this, "Pencarian tidak ada", Toast.LENGTH_SHORT).show()
        } else {
            adapter.setFilteredModels(filterArray)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}