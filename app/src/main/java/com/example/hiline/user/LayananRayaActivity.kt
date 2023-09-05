package com.example.hiline.user


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.LayananRayaUserAdapter
import com.example.hiline.api.HospitalApi
import com.example.hiline.interfaces.LayananRayaInterface
import com.example.hiline.model.HospitalModel
import com.example.hiline.model.HospitalsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class LayananRayaActivity : AppCompatActivity(), LayananRayaInterface {

    private lateinit var hospitalModels: ArrayList<HospitalModel>
    private lateinit var rvLayananRaya: RecyclerView
    private lateinit var adapter: LayananRayaUserAdapter
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan_raya)

        val svLayananRaya:SearchView = findViewById(R.id.svLayananRaya)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        Latitude = 0.0
        Longitude = 0.0

        hospitalModels = ArrayList()
        getHospital(latitude = Latitude, longitude = Longitude)
        rvLayananRaya = findViewById(R.id.rvLayananRaya)
        adapter = LayananRayaUserAdapter(hospitalModels, this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Longitude = location.longitude
                Latitude = location.latitude
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
                filterList(newText)
                return true
            }
        })

        btnBack.setOnClickListener {
            val intent = Intent(this, MainUserActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    fun getHospital(latitude: Double, longitude: Double) {
        val retrofit = Retro().getRetroClientInstance()
        val service = retrofit.create(HospitalApi::class.java)

        val call = service.getHospitals(latitude, longitude)
        call.enqueue(object : Callback<HospitalsResponse> {
            override fun onResponse(call: Call<HospitalsResponse>, response: Response<HospitalsResponse>) {
                if (response.isSuccessful) {
                    val hospitalResponse = response.body()
                    val hospitals = hospitalResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    hospitals?.forEach { hospital: HospitalsResponse.hospital ->
                        hospitalModels.add(
                            HospitalModel(
                                hospital.id,
                                hospital.nama,
                                hospital.kota,
                                hospital.provinsi,
                                hospital.alamat,
                                hospital.telepon,
                                hospital.image,
                                hospital.latitude,
                                hospital.longitude,
                                hospital.jarak
                            )
                        )
                        rvLayananRaya.adapter = adapter
                        rvLayananRaya.layoutManager = LinearLayoutManager(this@LayananRayaActivity)
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }

            override fun onFailure(call: Call<HospitalsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
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
    }

    override fun onItemClick(position: Int) {
        val rs = hospitalModels[position]
        val intent = Intent(this, LayananRayaDetailActivity::class.java)
        intent.putExtra("id", rs.id)
        intent.putExtra("nama", rs.nama)
        intent.putExtra("alamat", rs.alamat)
        intent.putExtra("telepon", rs.telepon)
        intent.putExtra("latitude", rs.latitude)
        intent.putExtra("longitude", rs.longitude)
        intent.putExtra("jarak", rs.jarak)
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
        val intent = Intent(this, MainUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}