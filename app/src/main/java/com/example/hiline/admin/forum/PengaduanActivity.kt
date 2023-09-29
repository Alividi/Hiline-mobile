package com.example.hiline.admin.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ReportAdminAdapter
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.ForumService
import com.example.hiline.interfaces.ReportInterface
import com.example.hiline.model.ReportModel
import com.example.hiline.response.ReportsResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaduanActivity : AppCompatActivity(), ReportInterface {

    private lateinit var prefManager: PrefManager
    private lateinit var reportModels: ArrayList<ReportModel>
    private lateinit var rvPengaduan: RecyclerView
    private lateinit var adapter: ReportAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaduan)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)

        reportModels = ArrayList()
        getReports()
        rvPengaduan = findViewById(R.id.rvPengaduan)
        adapter = ReportAdminAdapter(reportModels, this)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getReports(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()
        val call = service.getReportsAdmin(aToken)
        call.enqueue(object : Callback<ReportsResponse> {
            override fun onResponse(call: Call<ReportsResponse>, response: Response<ReportsResponse>) {
                if (response.isSuccessful) {
                    val reportsResponse = response.body()
                    val reports = reportsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    reports?.forEach {report ->
                        reportModels.add(
                            ReportModel(
                                report.id,
                                report.comment?.id,
                                report.pelapor?.id,
                                report.pelapor?.name,
                                report.pelapor?.username,
                                report.pelapor?.email,
                                report.pelapor?.role,
                                report.pelapor?.tanggalLahir,
                                report.pelapor?.image,
                                report.terlapor?.id,
                                report.terlapor?.name,
                                report.terlapor?.username,
                                report.terlapor?.email,
                                report.terlapor?.role,
                                report.terlapor?.tanggalLahir,
                                report.terlapor?.image,
                                report.message,
                                report.view_history,
                                report.hour,
                                report.date
                            )
                        )
                        rvPengaduan.adapter = adapter
                        rvPengaduan.layoutManager = LinearLayoutManager(this@PengaduanActivity)
                        adapter.notifyDataSetChanged()
                    }

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

    override fun onItemClick(position: Int) {
        val intent = Intent(this, PengaduanInfoActivity::class.java)
        intent.putExtra("idReport", reportModels[position].id)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}