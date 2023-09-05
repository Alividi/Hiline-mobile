package com.example.hiline.user

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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.ReportResponse
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
        val token = "Bearer ${prefManager.getToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.deleteReport(id,token)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, RiwayatPengaduanActivity::class.java)
        startActivity(intent)
        finish()
    }
}