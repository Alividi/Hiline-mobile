package com.example.hiline.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.ReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaduanInfoActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var id: String
    private lateinit var idComment: String
    private lateinit var tvVJam: TextView
    private lateinit var tvVTanggal: TextView
    private lateinit var tvVAlasan: TextView
    private lateinit var tvVPengirim: TextView
    private lateinit var tvVUnamePengirim: TextView
    private lateinit var tvVTerlapor: TextView
    private lateinit var tvVUnameTerlapor: TextView
    private lateinit var btnLihatKomen: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaduan_info)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnLihatKomen = findViewById(R.id.btnLihatKomen)
        val btnhapus: TextView = findViewById(R.id.btnhapus)
        tvVJam = findViewById(R.id.tvVJam)
        tvVTanggal = findViewById(R.id.tvVTanggal)
        tvVAlasan = findViewById(R.id.tvVAlasan)
        tvVPengirim = findViewById(R.id.tvVPengirim)
        tvVUnamePengirim = findViewById(R.id.tvVUnamePengirim)
        tvVTerlapor = findViewById(R.id.tvVTerlapor)
        tvVUnameTerlapor = findViewById(R.id.tvVUnameTerlapor)

        idComment = ""
        id = intent.getStringExtra("idReport").toString()
        getReport()

        btnBack.setOnClickListener {
            val intent = Intent(this, PengaduanActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLihatKomen.setOnClickListener {
            val intent = Intent(this, PengaduanKomentarActivity::class.java)
            intent.putExtra("idComment", idComment)
            startActivity(intent)
            finish()
        }

        btnhapus.setOnClickListener {
            showDeleteDialog()
        }
    }

    fun getReport(){
        val token = "Bearer ${prefManager.getAccessToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.getReport(id,token)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.isSuccessful) {
                    val report = response.body()?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    idComment = report?.comment_id.toString()
                    tvVJam.text = report?.jam
                    tvVTanggal.text = report?.tanggal
                    tvVAlasan.text = report?.message
                    tvVPengirim.text = report?.pelapor?.nama
                    tvVUnamePengirim.text = "@"+report?.pelapor?.username
                    tvVTerlapor.text = report?.terlapor?.nama
                    tvVUnameTerlapor.text = "@"+report?.terlapor?.username

                    if (report?.comment_deleted == true){
                        btnLihatKomen.visibility = View.GONE
                    }
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

    fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        val tvTitle: TextView = dialog.findViewById(R.id.tvTitle)
        val tvKet: TextView = dialog.findViewById(R.id.tvKet)
        dialog.setTitle("Hapus Pengaduan")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        tvTitle.text = "Hapus Pengaduan?"
        tvKet.text = "Aksi ini tidak dapat dibatalkan dan akan dihilangkan dari Pengaduan."

        btnHapus.setOnClickListener {
            deleteReport()
            val intent = Intent(this, PengaduanActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteReport(){
        val token = "Bearer ${prefManager.getAccessToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.deleteReport(id,token)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.isSuccessful) {
                    val report = response.body()?.data
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
        val intent = Intent(this, PengaduanActivity::class.java)
        startActivity(intent)
        finish()
    }
}