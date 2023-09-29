package com.example.hiline.admin.edukasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.hiline.R

class EdukasiAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_admin)

        val btnBack: ImageView = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}