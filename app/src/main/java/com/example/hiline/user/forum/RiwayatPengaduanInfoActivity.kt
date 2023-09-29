package com.example.hiline.user.forum

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.model.ReportModel
import com.example.hiline.service.Retro
import com.example.hiline.service.ForumService
import com.example.hiline.response.ReportResponse
import com.example.hiline.response.ReportsResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPengaduanInfoActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pengaduan_info)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnHapus: AppCompatButton = findViewById(R.id.btnHapus)
        val tvVJam: TextView = findViewById(R.id.tvVJam)
        val tvVTanggal: TextView = findViewById(R.id.tvVTanggal)
        val tvVAlasan: TextView = findViewById(R.id.tvVAlasan)
        val tvVTelapor: TextView = findViewById(R.id.tvVTelapor)

        tvVJam.text = intent.getStringExtra("jam")
        tvVTanggal.text = intent.getStringExtra("tanggal")
        tvVAlasan.text = intent.getStringExtra("alasan")
        tvVTelapor.text = "@" + intent.getStringExtra("terlapor")

        btnBack.setOnClickListener {
            val intent = Intent(this, RiwayatPengaduanActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHapus.setOnClickListener {
            showDialog()
        }
    }

    fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_pengaduan)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        dialog.setTitle("Hapus Riwayat")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        btnHapus.setOnClickListener {
            deleteReport()
            val intent = Intent(this@RiwayatPengaduanInfoActivity, RiwayatPengaduanActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteReport(){
        val id = intent.getStringExtra("id").toString()
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.deleteReport(id,aToken)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    val reportsResponse = response.body()
                    val reports = reportsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    Log.e("Status: ", response.body()?.status.toString())
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, RiwayatPengaduanActivity::class.java)
        startActivity(intent)
        finish()
    }
}