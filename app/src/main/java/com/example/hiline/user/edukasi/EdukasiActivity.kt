package com.example.hiline.user.edukasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.adapter.edukasi.EducationUserAdapter
import com.example.hiline.adapter.forum.ForumRayaUserAdapter
import com.example.hiline.interfaces.EducationInterface
import com.example.hiline.model.EducationModel
import com.example.hiline.model.ForumModel
import com.example.hiline.response.EducationsResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.service.EducationService
import com.example.hiline.service.ForumService
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.MainUserActivity
import com.example.hiline.user.forum.ForumRayaActivity
import com.example.hiline.user.forum.ForumRayaKomentarActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EdukasiActivity : AppCompatActivity(), EducationInterface {

    private lateinit var edukasiModels: ArrayList<EducationModel>
    private lateinit var rvEdukasi: RecyclerView
    private lateinit var adapter: EducationUserAdapter
    private var totalPage = 1
    private var currentPage = 1
    private val LOAD_MORE_THRESHOLD = 1
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var prefManager: PrefManager
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi)
        prefManager = PrefManager(this)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvFilterAll: TextView = findViewById(R.id.tvFilterAll)
        val tvFilterMoF: TextView = findViewById(R.id.tvFilterMoF)
        val tvFilterDtK: TextView = findViewById(R.id.tvFilterDtK)

        edukasiModels = ArrayList()
        getEdukasi(page = 1)
        rvEdukasi = findViewById(R.id.rvEdukasi)
        layoutManager = GridLayoutManager(this@EdukasiActivity,2)
        rvEdukasi.layoutManager = layoutManager
        adapter = EducationUserAdapter(this, edukasiModels, this@EdukasiActivity)
        rvEdukasi.adapter = adapter

        rvEdukasi.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        tvFilterAll.setOnClickListener {
            tvFilterAll.setBackgroundResource(R.drawable.filter_selected_bg)
            tvFilterAll.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvFilterMoF.setBackgroundResource(R.drawable.filter_bg)
            tvFilterMoF.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFilterDtK.setBackgroundResource(R.drawable.filter_bg)
            tvFilterDtK.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            edukasiModels.clear()
            getEdukasi(page = 1)
        }
        tvFilterMoF.setOnClickListener {
            tvFilterMoF.setBackgroundResource(R.drawable.filter_selected_bg)
            tvFilterMoF.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvFilterAll.setBackgroundResource(R.drawable.filter_bg)
            tvFilterAll.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFilterDtK.setBackgroundResource(R.drawable.filter_bg)
            tvFilterDtK.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            edukasiModels.clear()
            getEdukasiFilter(page = 1, category = 1)
        }
        tvFilterDtK.setOnClickListener {
            tvFilterDtK.setBackgroundResource(R.drawable.filter_selected_bg)
            tvFilterDtK.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvFilterMoF.setBackgroundResource(R.drawable.filter_bg)
            tvFilterMoF.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFilterAll.setBackgroundResource(R.drawable.filter_bg)
            tvFilterAll.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            edukasiModels.clear()
            getEdukasiFilter(page = 1, category = 2)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadNextPage() {
        if (currentPage < totalPage) {
            currentPage++
            getEdukasi(currentPage)
        }
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
                    totalPage = edukasiResponse?.data?.totalPage ?: 1

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

    fun getEdukasiFilter(page: Int, category: Int){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroEduUrl(okHttpClient)
        val service = retrofit.create(EducationService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getEducationsFilter(page,category,aToken)
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
                    totalPage = edukasiResponse?.data?.totalPage ?: 1

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

    override fun onBackPressed() {
        val intent = Intent(this, MainUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}