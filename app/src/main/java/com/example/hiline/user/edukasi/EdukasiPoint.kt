package com.example.hiline.user.edukasi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.R
import com.example.hiline.service.PrefManager
import kotlin.properties.Delegates

class EdukasiPoint : AppCompatActivity() {

    private lateinit var tvMessage: TextView
    private lateinit var idEdukasi: String
    private lateinit var prefManager: PrefManager
    private var point by Delegates.notNull<Int>()
    private lateinit var btnOther: AppCompatButton
    private lateinit var btnBeranda: AppCompatButton
    private var result = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_point)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        idEdukasi = intent.getStringExtra("edukasiId").toString()
        result = intent.getIntExtra("result",0)
        tvMessage = findViewById(R.id.tvMessage)
        point = intent.getIntExtra("point", 0)
        Log.e("point",point.toString())
        btnOther = findViewById(R.id.btnOther)
        btnBeranda = findViewById(R.id.btnBeranda)

        if (result > 0){
            tvMessage.text = "Anda sudah pernah mengerjakan, 0 poin!"
        }else{
            tvMessage.text = "${tvMessage.text} ${point} poin!"
        }

        val link = intent.getStringExtra("link")

        if (!link.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        } else {
            Log.e("EdukasiPoint", "Empty or null link")
        }

        btnBeranda.setOnClickListener {
            val intent = Intent(this, EdukasiActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnOther.setOnClickListener {
            val intent = Intent(this, EdukasiLinks::class.java)
            intent.putExtra("edukasiId", idEdukasi)
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