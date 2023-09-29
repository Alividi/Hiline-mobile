package com.example.hiline

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.UserService
import com.example.hiline.request.LoginRequest
import com.example.hiline.response.UserResponse
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.user.MainUserActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvLupaPw: TextView = findViewById(R.id.tvLupaPw)
        val btnMasuk:AppCompatButton = findViewById(R.id.btnMasuk)
        val btnDaftar:TextView = findViewById(R.id.tvBuatAkun)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)

        prefManager = PrefManager(this)

        btnMasuk.setOnClickListener {
            signIn()
        }
        tvLupaPw.setOnClickListener {
            val intent = Intent(this, LupaPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun signIn() {
        val request = LoginRequest()
        request.username = etUsername.text.toString().trim()
        request.password = etPassword.text.toString().trim()

        val retro = Retro().getRetroAuthUrl().create(UserService::class.java)

        if (etUsername.text.toString() == ""){
            etUsername.error = "Username wajib diisi"
            etUsername.requestFocus()
        } else if (etPassword.text.toString() == ""){
            etPassword.error = "Password wajib diisi"
            etPassword.requestFocus()
        } else {
            retro.login(request).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        //val gson = GsonBuilder().setPrettyPrinting().create()
                        //val responseBody = gson.toJson(response.body())
                        //Log.e("Body: ", responseBody)
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        val user = response.body()
                        if (user != null) {
                            prefManager.setId(user.data?.user?.id.toString())
                            prefManager.setAccessToken(user.data?.access_token.toString())
                            prefManager.setRefreshToken(user.data?.refresh_token.toString())
                            prefManager.setNama(user.data?.user?.name.toString())
                            prefManager.setUsername(user.data?.user?.username.toString())
                            prefManager.setEmail(user.data?.user?.email.toString())
                            prefManager.setRole(user.data?.user?.role.toString())
                            prefManager.setTanggal(user.data?.user?.tanggal_lahir.toString())
                            prefManager.setPImg(user.data?.user?.image.toString())

                            if (user.data?.user?.role.toString() == "admin"){
                                val intent = Intent(this@LoginActivity, MainAdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            if (user.data?.user?.role.toString() == "user"){
                                val intent = Intent(this@LoginActivity, MainUserActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        showDialog()
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("Error: ", t.message ?: "Error not found")
                }
            })
        }
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        dialog.setTitle("Login gagal")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDialogGPw.text = "Username atau password yang dimasukkan belum sesuai"
        dialog.show()
    }
}