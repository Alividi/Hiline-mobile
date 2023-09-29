package com.example.hiline.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.adapter.edukasi.EducationHomeAdapter
import com.example.hiline.adapter.edukasi.EducationUserAdapter
import com.example.hiline.interfaces.EducationInterface
import com.example.hiline.model.EducationModel
import com.example.hiline.model.ForumModel
import com.example.hiline.response.CurrentResponse
import com.example.hiline.response.EducationsResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.response.UserResponse
import com.example.hiline.service.AuthService
import com.example.hiline.service.EducationService
import com.example.hiline.service.HospitalService
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.edukasi.EdukasiActivity
import com.example.hiline.user.edukasi.EdukasiDtK
import com.example.hiline.user.edukasi.EdukasiMoF
import com.example.hiline.user.forum.ForumRayaActivity
import com.example.hiline.user.konsultasi.KonsultasiActivity
import com.example.hiline.user.layanan.LayananRayaActivity
import com.example.hiline.user.profile.ProfileUserActivity
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class MainUserActivity : AppCompatActivity(), EducationInterface {

    private lateinit var prefManager: PrefManager
    private lateinit var menuLayananRaya: ConstraintLayout
    private lateinit var menuForumRaya: ConstraintLayout
    private lateinit var menuKonsultasi: ConstraintLayout
    private lateinit var edukasiModels: ArrayList<EducationModel>
    private lateinit var rvEdukasi: RecyclerView
    private lateinit var adapter: EducationHomeAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var ivMember: ImageView
    private lateinit var tvMember: TextView
    private lateinit var pbMember: ProgressBar
    private lateinit var tvProgressPoint: TextView
    private lateinit var tvPoint: TextView
    private lateinit var grade: String
    private var point by Delegates.notNull<Int>()
    private lateinit var ivPP: ImageView
    private lateinit var tvNama: TextView
    private val handler = Handler()
    private val apiCall = Runnable { currentUser() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)
        prefManager = PrefManager(this)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        ivMember = findViewById(R.id.ivMember)
        tvMember = findViewById(R.id.tvMember)
        pbMember = findViewById(R.id.pbMember)
        tvProgressPoint = findViewById(R.id.tvProgressPoint)
        tvPoint = findViewById(R.id.tvPoint)

        ivPP = findViewById(R.id.ivPP)
        tvNama = findViewById(R.id.tvNama)
        val btnPelajari: AppCompatButton = findViewById(R.id.btnPelajari)
        menuLayananRaya = findViewById(R.id.menuLayananRaya)
        menuForumRaya = findViewById(R.id.menuForumRaya)
        menuKonsultasi = findViewById(R.id.menuKonsultasi)

        currentUser()
        //handler.removeCallbacks(apiCall)
        //xhandler.postDelayed(apiCall, 5000)

        edukasiModels = ArrayList()
        getEdukasi(page = 1)
        rvEdukasi = findViewById(R.id.rvEdukasiHome)
        layoutManager = LinearLayoutManager(this@MainUserActivity, LinearLayoutManager.HORIZONTAL, false)
        rvEdukasi.layoutManager = layoutManager
        adapter = EducationHomeAdapter(this, edukasiModels, this@MainUserActivity)
        rvEdukasi.adapter = adapter

        ivPP.setOnClickListener {
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPelajari.setOnClickListener {
            val intent = Intent(this, EdukasiActivity::class.java)
            startActivity(intent)
            finish()
        }

        menuLayananRaya.setOnClickListener {
            val intent = Intent(this, LayananRayaActivity::class.java)
            startActivity(intent)
            finish()
        }

        menuForumRaya.setOnClickListener {
            val intent = Intent(this, ForumRayaActivity::class.java)
            startActivity(intent)
            finish()
        }

        menuKonsultasi.setOnClickListener {
            val intent = Intent(this, KonsultasiActivity::class.java)
            startActivity(intent)
        }
    }

    fun currentUser(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getAuthUrl(okHttpClient)
        val service = retrofit.create(AuthService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.currentUser(aToken)

        call.enqueue(object : Callback<CurrentResponse> {
            override fun onResponse(call: Call<CurrentResponse>, response: Response<CurrentResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    grade = response.body()?.data?.user?.user_point?.grade.toString()
                    point = response.body()?.data?.user?.user_point?.point!!
                    tvNama.text = response.body()?.data?.user?.name.toString()
                    val imgUri = response.body()?.data?.user?.image
                    if (imgUri.isNullOrEmpty()){

                    }else{
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPP)
                    }

                    tvProgressPoint.text = point.toString()

                    if (grade == "BRONZE" || grade == ""){
                        tvMember.text = "Bronze"
                        tvMember.setTextColor(getColor(R.color.bronze))
                        ivMember.setImageResource(R.drawable.bronze_medal)
                        tvPoint.text = "/300 point"
                        pbMember.progressDrawable = ContextCompat.getDrawable(this@MainUserActivity,R.drawable.pb_bronze)
                        pbMember.max = 300
                        pbMember.progress = point
                    }
                    if (grade == "SILVER"){
                        tvMember.text = "Silver"
                        tvMember.setTextColor(getColor(R.color.silva))
                        ivMember.setImageResource(R.drawable.silver_medal)
                        tvPoint.text = "/800 point"
                        pbMember.progressDrawable = ContextCompat.getDrawable(this@MainUserActivity,R.drawable.pb_silver)
                        pbMember.max = 800
                        pbMember.progress = point
                    }
                    if (grade == "GOLD"){
                        tvMember.text = "Gold"
                        tvMember.setTextColor(getColor(R.color.yellow_orange))
                        ivMember.setImageResource(R.drawable.gold_medal)
                        tvPoint.text = "/1200 point"
                        pbMember.progressDrawable = ContextCompat.getDrawable(this@MainUserActivity,R.drawable.pb_gold)
                        pbMember.max = 1200
                        pbMember.progress = point
                    }

                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun getEdukasi(page: Int){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroEduUrl(okHttpClient)
        val service = retrofit.create(EducationService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getEducations(page,aToken)
        call.enqueue(object : Callback<EducationsResponse> {
            override fun onResponse(call: Call<EducationsResponse>, response: Response<EducationsResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    val edukasiResponse = response.body()
                    val educations = edukasiResponse?.data?.datas

                    educations?.forEach { education ->
                        edukasiModels.add(
                            EducationModel(
                                education.id,
                                education.image,
                                education.color,
                                education.title,
                                education.question,
                                education.answer,
                                education.result,
                                education.description,
                                education.article,
                                education.category?.id,
                                education.category?.serial,
                                education.category?.name
                            )
                        )
                    }
                    Log.e("Pagination", "Edukasi size: ${edukasiModels.size}")
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<EducationsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun onItemClick(position: Int) {
        if (edukasiModels[position].serial == "1"){
            val edukasiId = edukasiModels[position].id
            val intent = Intent(this, EdukasiMoF::class.java)
            intent.putExtra("edukasiId", edukasiId)
            startActivity(intent)
            finish()
        }
        if (edukasiModels[position].serial == "2"){
            val edukasiId = edukasiModels[position].id
            val intent = Intent(this, EdukasiDtK::class.java)
            intent.putExtra("edukasiId", edukasiId)
            startActivity(intent)
            finish()
        }
    }
}