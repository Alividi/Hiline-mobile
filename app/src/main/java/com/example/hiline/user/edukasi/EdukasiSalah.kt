package com.example.hiline.user.edukasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.R
import com.example.hiline.service.PrefManager

class EdukasiSalah : AppCompatActivity() {

    private  lateinit var btnPelajari: AppCompatButton
    private lateinit var prefManager: PrefManager
    private lateinit var idEdukasi: String
    private lateinit var deskripsi: String
    private lateinit var tvMessage: TextView
    private var result = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_salah)
        prefManager = PrefManager(this)

        idEdukasi = intent.getStringExtra("edukasiId").toString()
        deskripsi = intent.getStringExtra("deskripsi").toString()
        result = intent.getIntExtra("result",0)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnPelajari = findViewById(R.id.btnPelajari)
        tvMessage = findViewById(R.id.tvMessage)

        tvMessage.text = deskripsi

        btnPelajari.setOnClickListener {
            val intent = Intent(this, EdukasiLinks::class.java)
            intent.putExtra("edukasiId", idEdukasi)
            intent.putExtra("result",result)
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, EdukasiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, EdukasiActivity::class.java)
        startActivity(intent)
        finish()
    }
}