package com.example.hiline.admin

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
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.ForumRequest
import com.example.hiline.model.ForumResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaEditActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etIsi: EditText
    private lateinit var btnSimpan: AppCompatButton
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_edit)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        etJudul = findViewById(R.id.etJudul)
        etIsi = findViewById(R.id.etIsi)
        btnSimpan = findViewById(R.id.btnSimpan)
        Log.e("id: ",intent.getStringExtra("id").toString())
        Log.e("title: ",intent.getStringExtra("title").toString())
        Log.e("description: ",intent.getStringExtra("description").toString())

        val judul: String? = intent.getStringExtra("title").toString()
        etJudul.text = judul?.toEditable() ?: "".toEditable()
        val isi: String? = intent.getStringExtra("description").toString()
        etIsi.text = isi?.toEditable() ?: "".toEditable()

        updateButtonState()

        etJudul.addTextChangedListener(textWatcher)
        etIsi.addTextChangedListener(textWatcher)

        btnBack.setOnClickListener {
            val intent = Intent(this,ForumRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSimpan.setOnClickListener {
            putForum()
        }
    }
    fun putForum(){
        val request = ForumRequest()
        request.title = etJudul.text.toString().trim()
        request.description = etIsi.text.toString().trim()

        val retro = Retro().getRetroClientInstance().create(ForumApi::class.java)
        val tokenAuth = "Bearer ${prefManager.getToken()}"
        Log.e("Token: ",tokenAuth)

        val id = intent.getStringExtra("id").toString()

        if (etJudul.text.toString() == ""){
            etJudul.error = "Title wajib diisi"
            etJudul.requestFocus()
        } else if (etIsi.text.toString() == ""){
            etIsi.error = "Description wajib diisi"
            etIsi.requestFocus()
        } else {
            retro.putForum(id, tokenAuth, request).enqueue(object : Callback<ForumResponse> {
                override fun onResponse(
                    call: Call<ForumResponse>,
                    response: Response<ForumResponse>
                ) {
                    if (response.isSuccessful) {
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        showDialog()
                    }else{
                        val rawResponse = response.raw().toString()
                        Log.e("Raw Response: ", rawResponse)
                        Log.e("Error: ", "unsuccessful response")
                        Log.e("Error Code: ", response.code().toString() + response.message().toString())
                    }
                }

                override fun onFailure(call: Call<ForumResponse>, t: Throwable) {
                    Log.e("onFailure: ", t.toString())
                }

            })
        }
    }
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        val judul = etJudul.text.toString().trim()
        val isi = etIsi.text.toString().trim()

        val isButtonEnabled = judul.isNotEmpty() && isi.isNotEmpty()

        btnSimpan.isEnabled = isButtonEnabled
        val drawableRes = if (!isButtonEnabled) R.drawable.btn_disable_bg else R.drawable.btn_succes_bg
        val drawable = resources.getDrawable(drawableRes)
        btnSimpan.setBackgroundDrawable(drawable)
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        dialog.setTitle("Edit Topik Berhasil")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Topik forum berhasil diubah"

        dialog.setOnDismissListener {
            val intent = Intent(this@ForumRayaEditActivity, ForumRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this,ForumRayaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}