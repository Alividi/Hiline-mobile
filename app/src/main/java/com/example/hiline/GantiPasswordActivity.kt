package com.example.hiline

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
import com.example.hiline.service.PrefManager
import com.example.hiline.service.Retro
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.UserService
import com.example.hiline.response.ProfileResponse
import com.example.hiline.request.ValidateResetPWRequest
import com.example.hiline.response.AnswerResponse
import com.example.hiline.response.GantiPwResponse
import com.example.hiline.service.EducationService
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.profile.ProfileUserActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLanjut: AppCompatButton
    private lateinit var prefManager: PrefManager
    private var token: String = ""

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
            val tokenAuthenticator = TokenAuthenticator(prefManager)
            val okHttpClient = OkHttpClient.Builder()
                .authenticator(tokenAuthenticator)
                .build()
            val retrofit = Retro().getUserUrl(okHttpClient)
            val service = retrofit.create(UserService::class.java)
            val aToken = prefManager.getAccessToken()

            val call = service.validateResetPW(aToken,request)
            call.enqueue(object : Callback<GantiPwResponse> {
                override fun onResponse(call: Call<GantiPwResponse>, response: Response<GantiPwResponse>) {
                    if (response.isSuccessful) {
                        //val gson = GsonBuilder().setPrettyPrinting().create()
                        //val responseBody = gson.toJson(response.body())
                        //Log.e("Body: ", responseBody)
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        token = response.body()?.data?.token.toString()
                        if (token == "" ){
                            Log.e("token: ", "Token Kosong")
                        }else{
                            val intent = Intent(this@GantiPasswordActivity, GantiPwActivity::class.java)
                            Log.e("token: ", token)
                            intent.putExtra("token",token)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                        showDialog()
                    }
                }

                override fun onFailure(call: Call<GantiPwResponse>, t: Throwable) {
                    Log.e("Network API Error: ", t.message.toString())
                    Log.e("Error: ","network or API call failure")
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