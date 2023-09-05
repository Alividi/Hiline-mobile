package com.example.hiline.user

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.LoginActivity
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.api.UserApi
import com.example.hiline.model.ProfileResponse
import com.example.hiline.model.ResetPWRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPwActivity : AppCompatActivity() {

    private lateinit var btnSimpan: AppCompatButton
    private lateinit var prefManager: PrefManager
    private lateinit var etPassword: TextInputEditText
    private lateinit var etKPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_pw)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        etPassword = findViewById(R.id.etPassword)
        etKPassword = findViewById(R.id.etKPassword)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnBack.setOnClickListener {
            val intent = Intent(this, GantiPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSimpan.setOnClickListener {
            resetPW()
        }
    }

    fun resetPW(){
        val request = ResetPWRequest()
        request.new_password = etPassword.text.toString().trim()
        request.confirm_password = etKPassword.text.toString().trim()

        if (etPassword.text.toString() == "") {
            etPassword.error = "Password wajib diisi"
            etPassword.requestFocus()
        }else if (etKPassword.text.toString() == "") {
            etKPassword.error = "Wajib diisi"
            etKPassword.requestFocus()
        }else{
            val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
            val tokenAuth = "Bearer ${prefManager.getToken()}"

            retro.resetPW(tokenAuth, request).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        showDialog()
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                        Log.e("new: ", request.new_password.toString())
                        Log.e("confirm: ", request.confirm_password.toString())
                        showDialogGagal()
                    }
                }
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Error: ", t.message ?: "Error not found")
                }
            })
        }
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        dialog.setTitle("Ganti Password Berhasil")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Password telah berhasil diubah"

        dialog.setOnDismissListener {
            if (prefManager.getRole() == "user"){
                val intent = Intent(this@GantiPwActivity, ProfileUserActivity::class.java)
                startActivity(intent)
                finish()
            } else if (prefManager.getRole() == "admin"){
                val intent = Intent(this@GantiPwActivity, MainAdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        dialog.show()
    }

    fun showDialogGagal(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        dialog.setTitle("Ganti Password gagal")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDialogGPw.text = "Pastikan konfirmasi password sesuai"
        dialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, GantiPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }
}