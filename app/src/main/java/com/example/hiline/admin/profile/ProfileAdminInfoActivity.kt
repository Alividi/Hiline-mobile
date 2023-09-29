package com.example.hiline.admin.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.response.CurrentResponse
import com.example.hiline.service.AuthService
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileAdminInfoActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var ivPP: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private var name: String = ""
    private var username: String =""
    private var pImg: String = ""
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin_info)

        prefManager = PrefManager(this)

        val btnUbah: AppCompatButton = findViewById(R.id.btnUbah)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        tvNama = findViewById(R.id.tvNama)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        ivPP = findViewById(R.id.ivPP)

        currentUser()

        btnBack.setOnClickListener {
            val intent = Intent(this, MainAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnUbah.setOnClickListener {
            val intent = Intent(this, ProfileAdminEditActivity::class.java)
            startActivity(intent)
            finish()
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

                    name = response.body()?.data?.user?.name.toString()
                    username = response.body()?.data?.user?.username.toString()
                    pImg = response.body()?.data?.user?.image.toString()
                    email = response.body()?.data?.user?.email.toString()

                    tvNama.text = name
                    tvUsername.text = username
                    tvEmail.text = email
                    val imgUri = pImg
                    if (imgUri.isNullOrEmpty()){

                    }else{
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPP)
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

    override fun onBackPressed() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}
