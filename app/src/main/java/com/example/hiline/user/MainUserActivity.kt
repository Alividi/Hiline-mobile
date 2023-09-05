package com.example.hiline.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.RegisterActivity
import com.squareup.picasso.Picasso

class MainUserActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var menuLayananRaya: ConstraintLayout
    private lateinit var menuForumRaya: ConstraintLayout
    private lateinit var menuKonsultasi: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)
        prefManager = PrefManager(this)

        val ivPP:ImageView = findViewById(R.id.ivPP)
        val tvNama:TextView = findViewById(R.id.tvNama)
        val btnPelajari: AppCompatButton = findViewById(R.id.btnPelajari)
        menuLayananRaya = findViewById(R.id.menuLayananRaya)
        menuForumRaya = findViewById(R.id.menuForumRaya)
        menuKonsultasi = findViewById(R.id.menuKonsultasi)

        tvNama.text = prefManager.getNama()
        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

        ivPP.setOnClickListener {
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPelajari.setOnClickListener {
            val intent = Intent(this, EdukasiActivity::class.java)
            startActivity(intent)
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
}