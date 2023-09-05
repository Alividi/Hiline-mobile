package com.example.hiline.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.user.MainUserActivity
import com.example.hiline.user.ProfileUserEditActivity
import com.squareup.picasso.Picasso

class ProfileAdminInfoActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin_info)

        prefManager = PrefManager(this)

        val btnUbah: AppCompatButton = findViewById(R.id.btnUbah)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvNama: TextView = findViewById(R.id.tvNama)
        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val tvEmail: TextView = findViewById(R.id.tvEmail)
        val ivPP: ImageView = findViewById(R.id.ivPP)

        tvNama.text = prefManager.getNama()
        tvUsername.text = prefManager.getUsername()
        tvEmail.text = prefManager.getEmail()
        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

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
    override fun onBackPressed() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}
