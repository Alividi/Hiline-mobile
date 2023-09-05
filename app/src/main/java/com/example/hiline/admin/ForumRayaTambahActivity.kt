package com.example.hiline.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
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
import com.example.hiline.model.CommentResponse
import com.example.hiline.model.ForumRequest
import com.example.hiline.model.ForumResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaTambahActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etIsi: EditText
    private lateinit var btnTambah: AppCompatButton
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_tambah)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        etJudul = findViewById(R.id.etJudul)
        etIsi = findViewById(R.id.etIsi)
        btnTambah = findViewById(R.id.btnTambah)

        updateButtonState()

        etJudul.addTextChangedListener(textWatcher)
        etIsi.addTextChangedListener(textWatcher)

        btnBack.setOnClickListener {
            val intent = Intent(this, ForumRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnTambah.setOnClickListener {
            postForum()
        }
    }

    fun postForum(){
        val request = ForumRequest()
        request.title = etJudul.text.toString().trim()
        request.description = etIsi.text.toString().trim()

        val retro = Retro().getRetroClientInstance().create(ForumApi::class.java)
        val tokenAuth = "Bearer ${prefManager.getToken()}"

        if (etJudul.text.toString() == ""){
            etJudul.error = "Title wajib diisi"
            etJudul.requestFocus()
        } else if (etIsi.text.toString() == ""){
            etIsi.error = "Description wajib diisi"
            etIsi.requestFocus()
        } else {
            retro.postForum(tokenAuth, request).enqueue(object : Callback<ForumResponse>{
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

        btnTambah.isEnabled = isButtonEnabled
        val drawableRes = if (!isButtonEnabled) R.drawable.btn_disable_bg else R.drawable.btn_succes_bg
        val drawable = resources.getDrawable(drawableRes)
        btnTambah.setBackgroundDrawable(drawable)
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        dialog.setTitle("Tambah Topik Berhasil")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Topik forum telah terunggah"

        dialog.setOnDismissListener {
            val intent = Intent(this@ForumRayaTambahActivity, ForumRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this, ForumRayaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}