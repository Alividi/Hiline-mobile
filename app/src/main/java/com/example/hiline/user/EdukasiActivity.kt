package com.example.hiline.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.hiline.R

class EdukasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi)

        val btnBack: ImageView = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}