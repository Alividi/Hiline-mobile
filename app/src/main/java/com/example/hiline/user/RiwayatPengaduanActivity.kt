package com.example.hiline.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.ReportUserAdapter
import com.example.hiline.api.ForumApi
import com.example.hiline.interfaces.ReportInterface
import com.example.hiline.model.ReportModel
import com.example.hiline.model.ReportsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPengaduanActivity : AppCompatActivity(), ReportInterface {

    private lateinit var reportModels: ArrayList<ReportModel>
    private lateinit var rvPengaduan: RecyclerView
    private lateinit var adapter: ReportUserAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pengaduan)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)

        reportModels = ArrayList()
        getReports()
        rvPengaduan = findViewById(R.id.rvPengaduan)
        adapter = ReportUserAdapter(reportModels,this)

        btnBack.setOnClickListener {
            val intent = Intent(this, ProfileUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getReports(){
        val token = "Bearer ${prefManager.getAccessToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.getReports(token)
        call.enqueue(object : Callback<ReportsResponse>{
            override fun onResponse(
                call: Call<ReportsResponse>,
                response: Response<ReportsResponse>
            ) {
                if (response.isSuccessful) {
                    val reportsResponse = response.body()
                    val reports = reportsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    reports?.forEach {report: ReportsResponse.datas ->
                        reportModels.add(
                            ReportModel(
                                report.id,
                                report.comment_id,
                                report.pelapor?.id,
                                report.pelapor?.nama,
                                report.pelapor?.username,
                                report.pelapor?.email,
                                report.pelapor?.role,
                                report.pelapor?.tanggal_lahir,
                                report.pelapor?.profile_image,
                                report.terlapor?.id,
                                report.terlapor?.nama,
                                report.terlapor?.username,
                                report.terlapor?.email,
                                report.terlapor?.role,
                                report.terlapor?.tanggal_lahir,
                                report.terlapor?.profile_image,
                                report.message,
                                report.terproses,
                                report.jam,
                                report.tanggal
                            )
                        )
                        rvPengaduan.adapter = adapter
                        rvPengaduan.layoutManager = LinearLayoutManager(this@RiwayatPengaduanActivity)
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ReportsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, RiwayatPengaduanInfoActivity::class.java)
        intent.putExtra("id",reportModels[position].id)
        intent.putExtra("jam", reportModels[position].jam)
        intent.putExtra("tanggal", reportModels[position].tanggal)
        intent.putExtra("alasan", reportModels[position].message)
        intent.putExtra("terlapor", reportModels[position].tUsername)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}