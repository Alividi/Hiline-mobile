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
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.UserService
import com.example.hiline.response.ProfileResponse
import com.example.hiline.request.ResetPWRequest
import com.example.hiline.response.GantiPwResponse
import com.example.hiline.user.profile.ProfileUserActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPwActivity : AppCompatActivity() {

    private lateinit var btnSimpan: AppCompatButton
    private lateinit var prefManager: PrefManager
    private lateinit var etPassword: TextInputEditText
    private lateinit var etKPassword: TextInputEditText
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_pw)
        prefManager = PrefManager(this)

        token = intent.getStringExtra("token").toString()

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
        request.token = token
        request.password = etPassword.text.toString().trim()
        request.confirm_password = etKPassword.text.toString().trim()

        if (etPassword.text.toString() == "") {
            etPassword.error = "Password wajib diisi"
            etPassword.requestFocus()
        }else if (etKPassword.text.toString() == "") {
            etKPassword.error = "Wajib diisi"
            etKPassword.requestFocus()
        }else{
            val retrofit = Retro().getRetroUserUrl()
            val service = retrofit.create(UserService::class.java)
            val aToken = prefManager.getAccessToken()

            val call = service.resetPW(aToken,request)
            call.enqueue(object : Callback<GantiPwResponse> {
                override fun onResponse(call: Call<GantiPwResponse>, response: Response<GantiPwResponse>) {
                    if (response.isSuccessful) {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val responseBody = gson.toJson(response.body())
                        Log.e("Body: ", responseBody)
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        showDialog()
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                        showDialogGagal()
                    }
                }

                override fun onFailure(call: Call<GantiPwResponse>, t: Throwable) {
                    Log.e("Network API Error: ", t.message.toString())
                    Log.e("Error: ","network or API call failure")
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