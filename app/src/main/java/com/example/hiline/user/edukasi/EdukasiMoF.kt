package com.example.hiline.user.edukasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.R
import com.example.hiline.model.EducationModel
import com.example.hiline.request.LinkRequest
import com.example.hiline.request.MoFRequest
import com.example.hiline.response.AnswerResponse
import com.example.hiline.response.EducationResponse
import com.example.hiline.response.EducationsResponse
import com.example.hiline.service.EducationService
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.MainUserActivity
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class EdukasiMoF : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var idEdukasi: String
    private lateinit var btnMitos: AppCompatButton
    private lateinit var btnFakta: AppCompatButton
    private lateinit var ivMoF: ImageView
    private lateinit var tvMoF: TextView
    private var answer = false
    private var userAnswer = false
    private var point by Delegates.notNull<Int>()
    private lateinit var deskripsi: String
    private var result = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_mof)
        prefManager = PrefManager(this)

        idEdukasi = intent.getStringExtra("edukasiId").toString()
        btnMitos = findViewById(R.id.btnMitos)
        btnFakta = findViewById(R.id.btnFakta)
        ivMoF = findViewById(R.id.ivMoF)
        tvMoF = findViewById(R.id.tvMoF)
        val btnBack: ImageView = findViewById(R.id.btnBack)

        getEdukasi()

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
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    tvMoF.text = response.body()?.data?.question
                    result = response.body()?.data?.result!!

                    val imgUri = response.body()?.data?.image

                    if (!imgUri.isNullOrEmpty()) {
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivMoF)
                    } else {
                        ivMoF.setImageResource(R.drawable.edukasi_def_mof)
                    }
                    deskripsi = response.body()?.data?.description.toString()

                    answer = response.body()?.data?.answer == true
                    Log.e("answer: ", answer.toString())

                    btnMitos.setOnClickListener {
                        userAnswer = false
                        if (userAnswer == answer){
                            getPoint()
                            point = 150
                            Log.e("point:", point.toString())
                            val intent = Intent(this@EdukasiMoF, EdukasiPoint::class.java)
                            intent.putExtra("point", point)
                            intent.putExtra("edukasiId", idEdukasi)
                            intent.putExtra("result",result)
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this@EdukasiMoF, EdukasiSalah::class.java)
                            intent.putExtra("edukasiId", idEdukasi)
                            intent.putExtra("deskripsi",deskripsi)
                            intent.putExtra("result",result)
                            startActivity(intent)
                            finish()
                        }
                    }

                    btnFakta.setOnClickListener {
                        userAnswer = true
                        if (userAnswer == answer){
                            getPoint()
                            point=150
                            Log.e("point:", point.toString())
                            val intent = Intent(this@EdukasiMoF, EdukasiPoint::class.java)
                            intent.putExtra("point", point)
                            intent.putExtra("edukasiId", idEdukasi)
                            intent.putExtra("result",result)
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this@EdukasiMoF, EdukasiSalah::class.java)
                            intent.putExtra("edukasiId", idEdukasi)
                            intent.putExtra("deskripsi",deskripsi)
                            intent.putExtra("result",result)
                            startActivity(intent)
                            finish()
                        }
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

    fun getPoint(){
        val request = MoFRequest()
        request.education_id = idEdukasi
        request.user_answer = userAnswer
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroEduUrl(okHttpClient)
        val service = retrofit.create(EducationService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.answerMoF(request,aToken)
        call.enqueue(object : Callback<AnswerResponse> {
            override fun onResponse(call: Call<AnswerResponse>, response: Response<AnswerResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
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