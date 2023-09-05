package com.example.hiline.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class ProfileUserActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var clLogout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)
        prefManager = PrefManager(this)

        val tvNama: TextView = findViewById(R.id.tvNama)
        val ivPP: ImageView = findViewById(R.id.ivPP)
        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val btnProfile: ImageView = findViewById(R.id.btnProfile)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnGPw: ImageView = findViewById(R.id.btnGPw)
        val btnPengaduan: ImageView = findViewById(R.id.btnPengaduan)
        clLogout = findViewById(R.id.clLogout)

        tvNama.text = prefManager.getNama()
        tvUsername.text = "@"+prefManager.getUsername()
        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

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