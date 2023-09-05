package com.example.hiline.user

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hiline.MainActivity
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.squareup.picasso.Picasso

class ProfileUserInfoActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_info)
        prefManager = PrefManager(this)

        val btnUbah: AppCompatButton = findViewById(R.id.btnUbah)
        val ivPP: ImageView = findViewById(R.id.ivPP)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvNama: TextView = findViewById(R.id.tvNama)
        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val tvEmail: TextView = findViewById(R.id.tvEmail)
        val tvTanggal: TextView = findViewById(R.id.tvTanggal)

        tvNama.text = prefManager.getNama()
        tvUsername.text = prefManager.getUsername()
        tvEmail.text = prefManager.getEmail()
        tvTanggal.text = prefManager.getTanggal()
        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

        btnBack.setOnClickListener {
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnUbah.setOnClickListener {
            val intent = Intent(this, ProfileUserEditActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}