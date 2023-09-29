package com.example.hiline

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.request.LoginRequest
import com.example.hiline.request.LupaPwRequest
import com.example.hiline.response.UserResponse
import com.example.hiline.service.Retro
import com.example.hiline.service.UserService
import com.example.hiline.user.MainUserActivity
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnLupaPw: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        etEmail = findViewById(R.id.etEmail)
        btnLupaPw = findViewById(R.id.btnLupaPw)

        btnLupaPw.setOnClickListener {
            lupaPassword()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun lupaPassword(){
        val request = LupaPwRequest()
        request.email = etEmail.text.toString().trim()

        val retro = Retro().getRetroUserUrl().create(UserService::class.java)

        retro.lupaPassword(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    showDialog()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Toast.makeText(this@LupaPasswordActivity, "Pastikan Email Anda Benar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("Error: ", t.message ?: "Error not found")
            }
        })
    }

    fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_register)
        dialog.setTitle("Reset Password Berhasil")
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val tvKetSucces: TextView = dialog.findViewById(R.id.tvKetSucces)
        val btnMasuk: AppCompatButton = dialog.findViewById(R.id.btnMasuk)

        tvKetSucces.text = "Link reset password telah dikrimkan ke email berikut: " + etEmail.text.toString()

        btnMasuk.setOnClickListener {
            val intent = Intent(this@LupaPasswordActivity, LoginActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }
    }
}