package com.example.hiline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.PrefManager
import com.example.hiline.user.MainUserActivity

class SplashscreenActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        prefManager = PrefManager(this)
        var role: String? = prefManager.getRole()
        var token: String? = prefManager.getAccessToken()

        Handler(Looper.getMainLooper()).postDelayed({
            if (token == "" || token == null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                if (role == "admin"){
                    val intent = Intent(this, MainAdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                if (role == "user"){
                    val intent = Intent(this, MainUserActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, 3000)

    }
}