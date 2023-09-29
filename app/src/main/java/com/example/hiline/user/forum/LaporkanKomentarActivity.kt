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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.service.ForumService
import com.example.hiline.request.ReportRequest
import com.example.hiline.response.ForumResponse
import com.example.hiline.response.ReportResponse
import com.example.hiline.response.ReportsResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaporkanKomentarActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvAlasan1: TextView
    private lateinit var tvAlasan2: TextView
    private lateinit var tvAlasan3: TextView
    private lateinit var tvAlasan4: TextView
    private lateinit var tvAlasan5: TextView
    private lateinit var tvAlasanLainnya: TextView
    private lateinit var btnLanjut: AppCompatButton
    private lateinit var idComment: String
    private lateinit var messageComment: String
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporkan_komentar)
        prefManager = PrefManager(this)

        btnBack = findViewById(R.id.btnBack)
        tvAlasan1 = findViewById(R.id.tvAlasan1)
        tvAlasan2 = findViewById(R.id.tvAlasan2)
        tvAlasan3 = findViewById(R.id.tvAlasan3)
        tvAlasan4 = findViewById(R.id.tvAlasan4)
        tvAlasan5 = findViewById(R.id.tvAlasan5)
        tvAlasanLainnya = findViewById(R.id.tvAlasanLainnya)
        btnLanjut = findViewById(R.id.btnLanjut)
        idComment = intent.getStringExtra("id").toString()

        tvAlasan1.setOnClickListener {
            tvAlasan1.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan2.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan3.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan4.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan5.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)
            messageComment = tvAlasan1.text.toString()
            Log.e("Message: ", messageComment)

            btnLanjut.setOnClickListener {
                createReport()
            }
        }

        tvAlasan2.setOnClickListener {
            tvAlasan2.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan1.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan3.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan4.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan5.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)
            messageComment = tvAlasan2.text.toString()
            Log.e("Message: ", messageComment)

            btnLanjut.setOnClickListener {
                createReport()
            }
        }

        tvAlasan3.setOnClickListener {
            tvAlasan3.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan1.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan2.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan4.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan5.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)
            messageComment = tvAlasan3.text.toString()
            Log.e("Message: ", messageComment)

            btnLanjut.setOnClickListener {
                createReport()
            }
        }

        tvAlasan4.setOnClickListener {
            tvAlasan4.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan1.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan2.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan3.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan5.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)
            messageComment = tvAlasan4.text.toString()
            Log.e("Message: ", messageComment)

            btnLanjut.setOnClickListener {
                createReport()
            }
        }

        tvAlasan5.setOnClickListener {
            tvAlasan5.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan1.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan2.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan3.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan4.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)
            messageComment = tvAlasan5.text.toString()
            Log.e("Message: ", messageComment)

            btnLanjut.setOnClickListener {
                createReport()
            }

        }

        tvAlasanLainnya.setOnClickListener {
            tvAlasanLainnya.setBackgroundResource(R.drawable.alasan_selected_bg)
            tvAlasan1.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan2.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan3.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan4.setBackgroundResource(R.drawable.alasan_bg)
            tvAlasan5.setBackgroundResource(R.drawable.alasan_bg)
            btnLanjut.setBackgroundResource(R.drawable.btn_apricot_bg)

            btnLanjut.setOnClickListener {
                val intent = Intent(this, LaporkanKomentarLainnyaActivity::class.java)
                intent.putExtra("idComment",idComment)
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, ForumRayaActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun createReport(){
        val request = ReportRequest()
        request.message = messageComment

        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.createReport(idComment,aToken,request)
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
            val intent = Intent(this@LaporkanKomentarActivity, ForumRayaActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, ForumRayaActivity::class.java)
        startActivity(intent)
        finish()
    }
}