package com.example.hiline.user

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.api.UserApi
import com.example.hiline.model.ProfileResponse
import com.example.hiline.model.ValidateResetPWRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLanjut: AppCompatButton
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_password)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnLanjut = findViewById(R.id.btnLanjut)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        updateButtonState()

        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        btnBack.setOnClickListener {
            if (prefManager.getRole() == "user"){
                val intent = Intent(this, ProfileUserActivity::class.java)
                startActivity(intent)
                finish()
            } else if (prefManager.getRole() == "admin"){
                val intent = Intent(this, MainAdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnLanjut.setOnClickListener {
            validateReset()
        }
    }

    fun validateReset(){
        val request = ValidateResetPWRequest()
        request.email = etEmail.text.toString().trim()
        request.password = etPassword.text.toString().trim()

        if (etEmail.text.toString() == "") {
            etEmail.error = "Email wajib diisi"
            etEmail.requestFocus()
        }else if (etPassword.text.toString() == "") {
            etPassword.error = "Password wajib diisi"
            etPassword.requestFocus()
        }else{
            val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
            val tokenAuth = "Bearer ${prefManager.getAccessToken()}"

            retro.validateResetPW(tokenAuth, request).enqueue(object : Callback<ProfileResponse>{
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        val intent = Intent(this@GantiPasswordActivity, GantiPwActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                        Log.e("email: ", request.email.toString())
                        Log.e("password: ", request.password.toString())
                        showDialog()
                    }
                }
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Error: ", t.message ?: "Error not found")
                }
            })
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        val isFieldsEmpty = etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()

        btnLanjut.isFocusable = !isFieldsEmpty
        btnLanjut.isClickable = !isFieldsEmpty

        val drawableRes = if (isFieldsEmpty) R.drawable.btn_disable_bg else R.drawable.btn_apricot_bg
        val drawable = resources.getDrawable(drawableRes)
        btnLanjut.setBackgroundDrawable(drawable)
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        dialog.setTitle("Ganti Password gagal")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onBackPressed() {
        if (prefManager.getRole() == "user"){
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
            finish()
        } else if (prefManager.getRole() == "admin"){
            val intent = Intent(this, MainAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}