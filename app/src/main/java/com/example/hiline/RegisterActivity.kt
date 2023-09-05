package com.example.hiline

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.api.UserApi
import com.example.hiline.model.RegisterRequest
import com.example.hiline.model.UserResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etKPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnDaftar: AppCompatButton = findViewById(R.id.btnDaftar)
        val tvMasuk: TextView = findViewById(R.id.tvMasuk)
        etNama = findViewById(R.id.etNama)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etKPassword = findViewById(R.id.etKPassword)

        btnDaftar.setOnClickListener {
            signUp()
        }
        tvMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signUp() {
        val request = RegisterRequest()
        request.name = etNama.text.toString().trim()
        request.username = etUsername.text.toString().trim()
        request.email = etEmail.text.toString().trim()
        request.password = etPassword.text.toString().trim()
        request.confirm_password = etKPassword.text.toString().trim()

        if (etNama.text.toString() == "") {
            etNama.error = "Nama wajib diisi"
            etNama.requestFocus()
        } else if (etEmail.text.toString() == "") {
            etEmail.error = "Email wajib diisi"
            etEmail.requestFocus()
        } else if (etUsername.text.toString() == "") {
            etUsername.error = "Username wajib diisi"
            etUsername.requestFocus()
        } else if (etPassword.text.toString() == "") {
            etPassword.error = "Password wajib diisi"
            etPassword.requestFocus()
        } else if (etKPassword.text.toString() == "") {
            etKPassword.error = "Wajib diisi"
            etKPassword.requestFocus()
        } else {
            val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
            retro.signup(request).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("name: ", request.name.toString())
                    Log.e("username: ", request.username.toString())
                    Log.e("email: ", request.email.toString())
                    Log.e("password: ", request.password.toString())
                    Log.e("k password: ", request.confirm_password.toString())
                    showDialog()
                    Toast.makeText(this@RegisterActivity, "Register Berhasil", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("Error: ", t.message ?: "Error not found")
                }

            })
        }
    }

    fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_register)
        dialog.setTitle("Registrasi Berhasil")
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val tvKetSucces: TextView = dialog.findViewById(R.id.tvKetSucces)
        val btnMasuk: AppCompatButton = dialog.findViewById(R.id.btnMasuk)

        tvKetSucces.text = tvKetSucces.text.toString() + etEmail.text.toString()

        btnMasuk.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }
    }
}