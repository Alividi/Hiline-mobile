package com.example.hiline.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ReportHomeAdminAdapter
import com.example.hiline.admin.edukasi.EdukasiAdminActivity
import com.example.hiline.admin.forum.ForumRayaAdminActivity
import com.example.hiline.admin.forum.PengaduanActivity
import com.example.hiline.admin.layanan.LayananRayaAdminActivity
import com.example.hiline.service.ForumService
import com.example.hiline.model.ReportModel
import com.example.hiline.response.ReportsResponse
import com.example.hiline.service.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var prefManager: PrefManager
    private lateinit var reportModels: ArrayList<ReportModel>
    private lateinit var rvPengaduan: RecyclerView
    private lateinit var adapter: ReportHomeAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context != null) {
            prefManager = PrefManager(requireContext())
        } else {
            Log.e("Eror: ", "Home Fragment no context")
        }

        val cvMenu1: CardView = view.findViewById(R.id.cvMenu1)
        val cvMenu2: CardView = view.findViewById(R.id.cvMenu2)
        val cvMenu3: CardView = view.findViewById(R.id.cvMenu3)
        val tvHallo: TextView = view.findViewById(R.id.tvHallo)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val formattedDate = currentDate.format(formatter)
        val tvSeluruhPengaduan: TextView = view.findViewById(R.id.tvSeluruhPengaduan)

        tvHallo.text = tvHallo.text.toString() + prefManager.getNama().toString()
        tvTanggal.text = formattedDate

        reportModels = ArrayList()
        getReports()
        rvPengaduan = view.findViewById(R.id.rvPengaduan)
        adapter = ReportHomeAdminAdapter(reportModels)

        tvSeluruhPengaduan.setOnClickListener {
            val intent = Intent(context, PengaduanActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        cvMenu1.setOnClickListener {
            val intent = Intent(context, ForumRayaAdminActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        cvMenu2.setOnClickListener {
            val intent = Intent(context, LayananRayaAdminActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        cvMenu3.setOnClickListener {
            val intent = Intent(context, EdukasiAdminActivity::class.java)
            startActivity(intent)
        }
    }
    fun getReports(){
        if (context != null) {
            prefManager = PrefManager(requireContext())
        } else {
            Log.e("Eror: ", "Home Fragment no context")
        }
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
                        rvPengaduan.layoutManager = LinearLayoutManager(requireContext())
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
}