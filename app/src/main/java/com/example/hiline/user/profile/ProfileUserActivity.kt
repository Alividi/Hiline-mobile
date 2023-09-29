package com.example.hiline.user.profile

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hiline.GantiPasswordActivity
import com.example.hiline.MainActivity
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.response.CurrentResponse
import com.example.hiline.service.AuthService
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.MainUserActivity
import com.example.hiline.user.forum.RiwayatPengaduanActivity
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUserActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var clLogout: ConstraintLayout
    private lateinit var clMember: ConstraintLayout
    private lateinit var dotMember: View
    private lateinit var tvNama: TextView
    private lateinit var ivPP: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvMember: TextView
    private var name: String = ""
    private var username: String =""
    private var pImg: String = ""
    private var grade: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)
        prefManager = PrefManager(this)

        tvNama = findViewById(R.id.tvNama)
        ivPP = findViewById(R.id.ivPP)
        tvUsername = findViewById(R.id.tvUsername)
        tvMember = findViewById(R.id.tvMember)
        clMember = findViewById(R.id.clMember)
        dotMember = findViewById(R.id.dotMember)
        val btnProfile: ImageView = findViewById(R.id.btnProfile)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnGPw: ImageView = findViewById(R.id.btnGPw)
        val btnPengaduan: ImageView = findViewById(R.id.btnPengaduan)
        clLogout = findViewById(R.id.clLogout)

        currentUser()

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileUserInfoActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnGPw.setOnClickListener {
            val intent = Intent(this, GantiPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPengaduan.setOnClickListener {
            val intent = Intent(this, RiwayatPengaduanActivity::class.java)
            startActivity(intent)
            finish()
        }

        clLogout.setOnClickListener {
            showLogoutDialog()
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
                    grade = response.body()?.data?.user?.user_point?.grade.toString()

                    if (grade == "BRONZE" || grade == ""){
                        tvMember.text = "Member Bronze"
                        tvMember.setTextColor(getColor(R.color.bronze))
                        dotMember.background = getDrawable(R.drawable.profileuser_dot_bronze_bg)
                        clMember.background = getDrawable(R.drawable.profileuser_member_bronze_bg)
                    }
                    if (grade == "SILVER"){
                        tvMember.text = "Member Silver"
                        tvMember.setTextColor(getColor(R.color.silva))
                        dotMember.background = getDrawable(R.drawable.profileuser_dot_silver_bg)
                        clMember.background = getDrawable(R.drawable.profileuser_member_silver_bg)
                    }
                    if (grade == "GOLD"){
                        tvMember.text = "Member Gold"
                        tvMember.setTextColor(getColor(R.color.yellow_orange))
                        dotMember.background = getDrawable(R.drawable.profileuser_dot_gold_bg)
                        clMember.background = getDrawable(R.drawable.profileuser_member_bg)
                    }

                    tvNama.text = name
                    tvUsername.text = "@${username}"
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

    fun logout(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getAuthUrl(okHttpClient)
        val service = retrofit.create(AuthService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.logout(aToken)

        call.enqueue(object : Callback<CurrentResponse> {
            override fun onResponse(call: Call<CurrentResponse>, response: Response<CurrentResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
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

    fun showLogoutDialog() {
        val dialogLogout = Dialog(this, R.style.MaterialDialogSheet)
        dialogLogout.setContentView(R.layout.dialog_logout)
        dialogLogout.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogLogout.window?.setGravity(Gravity.BOTTOM)
        dialogLogout.window?.attributes?.windowAnimations = R.style.MaterialDialogSheetAnimation
        dialogLogout.show()

        val btnLogoutDialog = dialogLogout.findViewById<AppCompatButton>(R.id.btnLogoutDialog)
        val btnKembaliDialog = dialogLogout.findViewById<TextView>(R.id.btnKembaliDialog)

        btnLogoutDialog.setOnClickListener {
            logout()
            prefManager.removeData()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialogLogout.dismiss()
            finish()
        }

        btnKembaliDialog.setOnClickListener {
            dialogLogout.dismiss()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}