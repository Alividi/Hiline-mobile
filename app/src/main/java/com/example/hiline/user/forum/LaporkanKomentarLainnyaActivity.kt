package com.example.hiline.user.forum

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
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.service.ForumService
import com.example.hiline.request.ReportRequest
import com.example.hiline.response.ReportResponse
import com.example.hiline.response.ReportsResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaporkanKomentarLainnyaActivity : AppCompatActivity() {

    private lateinit var etAlasan: EditText
    private lateinit var prefManager: PrefManager
    private lateinit var id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporkan_komentar_lainnya)
        prefManager = PrefManager(this)

        val btnLaporkan: AppCompatButton = findViewById(R.id.btnLaporkan)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        etAlasan = findViewById(R.id.etAlasan)

        id = intent.getStringExtra("idComment").toString()

        btnBack.setOnClickListener {
            val intent = Intent(this, LaporkanKomentarActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
            finish()
        }

        btnLaporkan.setOnClickListener {
            if (etAlasan.text.toString() == ""){
                etAlasan.error = "alasan wajib diisi"
                etAlasan.requestFocus()
            } else{
                createReport()
            }
        }
    }
    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ganti_password)
        val ivDialogGPw: ImageView = dialog.findViewById(R.id.ivDialogGPw)
        val tvDialogGPw: TextView = dialog.findViewById(R.id.tvDialogGPw)
        dialog.setTitle("Laporan Dikrim")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ivDialogGPw.setImageResource(R.drawable.ic_success)
        tvDialogGPw.text = "Laporan telah berhasil dikirim"

        dialog.setOnDismissListener {
            val intent = Intent(this@LaporkanKomentarLainnyaActivity, ForumRayaActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    fun createReport(){
        val request = ReportRequest()
        request.message = etAlasan.text.toString().trim()

        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.createReport(id,aToken,request)
        call.enqueue(object : Callback<ReportsResponse> {
            override fun onResponse(call: Call<ReportsResponse>, response: Response<ReportsResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    showDialog()
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    Log.e("Status: ", response.body()?.status.toString())
                }
            }
            override fun onFailure(call: Call<ReportsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, LaporkanKomentarActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
        finish()
    }
}