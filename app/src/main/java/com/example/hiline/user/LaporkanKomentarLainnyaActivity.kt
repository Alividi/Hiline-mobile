package com.example.hiline.user

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
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.ReportRequest
import com.example.hiline.model.ReportResponse
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
                postReports()
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

    fun postReports(){
        val request = ReportRequest()
        request.comment_id = id
        request.message = etAlasan.text.toString().trim()
        val token = "Bearer ${prefManager.getAccessToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.postReport(token,request)
        call.enqueue(object : Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    showDialog()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Toast.makeText(this@LaporkanKomentarLainnyaActivity,"Gagal",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
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