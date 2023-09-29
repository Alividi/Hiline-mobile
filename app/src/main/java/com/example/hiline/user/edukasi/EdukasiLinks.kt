package com.example.hiline.user.edukasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.R
import com.example.hiline.adapter.edukasi.LinkUserAdapter
import com.example.hiline.interfaces.EducationInterface
import com.example.hiline.model.LinkModel
import com.example.hiline.request.LinkRequest
import com.example.hiline.response.AnswerResponse
import com.example.hiline.response.EducationResponse
import com.example.hiline.service.EducationService
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EdukasiLinks : AppCompatActivity(), EducationInterface {

    private lateinit var linkModels: ArrayList<LinkModel>
    private lateinit var rvLinks: RecyclerView
    private lateinit var adapter: LinkUserAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var prefManager: PrefManager
    private lateinit var idEdukasi: String
    private var point = 0
    private var result = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_links)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        idEdukasi = intent.getStringExtra("edukasiId").toString()
        result = intent.getIntExtra("result",0)

        linkModels = ArrayList()
        getEdukasi()
        rvLinks = findViewById(R.id.rvLinks)
        layoutManager = LinearLayoutManager(this@EdukasiLinks)
        rvLinks.layoutManager = layoutManager
        adapter = LinkUserAdapter(this, linkModels, this@EdukasiLinks)
        rvLinks.adapter = adapter

        btnBack.setOnClickListener {
            val intent = Intent(this, EdukasiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getEdukasi(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroEduUrl(okHttpClient)
        val service = retrofit.create(EducationService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getEducation(idEdukasi,aToken)
        call.enqueue(object : Callback<EducationResponse> {
            override fun onResponse(call: Call<EducationResponse>, response: Response<EducationResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val edukasiResponse = response.body()
                    val links = edukasiResponse?.data?.sources

                    links?.forEach { link ->
                        linkModels.add(
                            LinkModel(
                                link.education_id,
                                link.id,
                                link.title,
                                link.link
                            )
                        )
                        adapter.notifyDataSetChanged()
                    }

                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")

                }
            }
            override fun onFailure(call: Call<EducationResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, EdukasiActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onItemClick(position: Int) {
        val link:String = linkModels[position].link.toString()
        getPoint()
        point = 50
        Log.e("point:", point.toString())
        val intent = Intent(this, EdukasiPoint::class.java)
        intent.putExtra("point", point)
        intent.putExtra("link",link)
        intent.putExtra("edukasiId", idEdukasi)
        intent.putExtra("result",result)
        startActivity(intent)
        finish()
    }

    fun getPoint(){
        val request = LinkRequest()
        request.education_id = idEdukasi
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroEduUrl(okHttpClient)
        val service = retrofit.create(EducationService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.answerLink(request,aToken)
        call.enqueue(object : Callback<AnswerResponse> {
            override fun onResponse(call: Call<AnswerResponse>, response: Response<AnswerResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    point = response.body()?.data?.score!!
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }

            override fun onFailure(call: Call<AnswerResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })

    }
}