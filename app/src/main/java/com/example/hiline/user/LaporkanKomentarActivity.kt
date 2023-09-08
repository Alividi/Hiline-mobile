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
                postReports()
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
                postReports()
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
                postReports()
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
                postReports()
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
                postReports()
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

    fun postReports(){
        val request = ReportRequest()
        request.comment_id = idComment
        request.message = messageComment
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
                    Toast.makeText(this@LaporkanKomentarActivity,"Gagal",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
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