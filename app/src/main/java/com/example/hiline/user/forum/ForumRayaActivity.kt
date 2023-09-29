package com.example.hiline.user.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ForumRayaUserAdapter
import com.example.hiline.adapter.layanan.LayananRayaUserAdapter
import com.example.hiline.service.ForumService
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.response.ForumFavResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.service.HospitalService
import com.example.hiline.service.TokenAuthenticator
import com.example.hiline.user.MainUserActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaActivity : AppCompatActivity(), ForumRayaInterface {

    private lateinit var forumModels: ArrayList<ForumModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumRayaUserAdapter
    private lateinit var prefManager: PrefManager
    private var totalPage = 1
    private var currentPage = 1
    private val LOAD_MORE_THRESHOLD = 1
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya)
        prefManager = PrefManager(this)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvTerbaru: TextView = findViewById(R.id.tvTerbaru)
        val tvPopuler: TextView = findViewById(R.id.tvPopuler)
        val tvFavorite: TextView = findViewById(R.id.tvFavorite)

        forumModels = ArrayList()
        getForums(page = 1)
        rvForumRaya = findViewById(R.id.rvForumRaya)
        layoutManager = LinearLayoutManager(this@ForumRayaActivity)
        rvForumRaya.layoutManager = layoutManager
        adapter = ForumRayaUserAdapter(this, forumModels,this@ForumRayaActivity)
        rvForumRaya.adapter = adapter

        rvForumRaya.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                val percentageScrolled =
                    (lastVisibleItemPosition + 1) / totalItemCount.toDouble()

                if (percentageScrolled >= LOAD_MORE_THRESHOLD && currentPage < totalPage) {
                    loadNextPage()
                }
            }
        })

        tvTerbaru.setOnClickListener {
            tvTerbaru.setBackgroundResource(R.drawable.filter_selected_bg)
            tvTerbaru.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvPopuler.setBackgroundResource(R.drawable.filter_bg)
            tvPopuler.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFavorite.setBackgroundResource(R.drawable.filter_bg)
            tvFavorite.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            forumModels.clear()
            getForums(page = 1)
        }
        tvPopuler.setOnClickListener {
            tvPopuler.setBackgroundResource(R.drawable.filter_selected_bg)
            tvPopuler.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvTerbaru.setBackgroundResource(R.drawable.filter_bg)
            tvTerbaru.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFavorite.setBackgroundResource(R.drawable.filter_bg)
            tvFavorite.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            forumModels.clear()
            getForumsPopular(page = 1)
        }
        tvFavorite.setOnClickListener {
            tvFavorite.setBackgroundResource(R.drawable.filter_selected_bg)
            tvFavorite.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvPopuler.setBackgroundResource(R.drawable.filter_bg)
            tvPopuler.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvTerbaru.setBackgroundResource(R.drawable.filter_bg)
            tvTerbaru.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            forumModels.clear()
            getForumsFavorite()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadNextPage() {
        if (currentPage < totalPage) {
            currentPage++
            getForums(currentPage)
        }
    }

    fun getForums(page: Int) {
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getForums(page,aToken)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val forumsResponse = response.body()
                    val forums = forumsResponse?.data?.forumData
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    totalPage = forumsResponse?.data?.totalPage ?: 1
                    forums?.forEach{ forum ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.author?.id,
                                forum.author?.name,
                                forum.author?.username,
                                forum.author?.email,
                                forum.author?.role,
                                forum.author?.tanggalLahir,
                                forum.author?.image,
                                forum.title,
                                forum.description,
                                forum.favoriteCount,
                                forum.commentCount,
                                forum.isFavorite
                            )
                        )
                    }
                    Log.e("Pagination", "HospitalModels size: ${forumModels.size}")
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    fun getForumsPopular(page: Int){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getForumsPop(page,aToken)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val forumsResponse = response.body()
                    val forums = forumsResponse?.data?.forumData
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    totalPage = forumsResponse?.data?.totalPage ?: 1
                    forums?.forEach{ forum ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.author?.id,
                                forum.author?.name,
                                forum.author?.username,
                                forum.author?.email,
                                forum.author?.role,
                                forum.author?.tanggalLahir,
                                forum.author?.image,
                                forum.title,
                                forum.description,
                                forum.favoriteCount,
                                forum.commentCount,
                                forum.isFavorite
                            )
                        )
                    }
                    Log.e("Pagination", "HospitalModels size: ${forumModels.size}")
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    fun getForumsFavorite(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getForumsFav(aToken)
        call.enqueue(object : Callback<ForumFavResponse> {
            override fun onResponse(call: Call<ForumFavResponse>, response: Response<ForumFavResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val forumsResponse = response.body()
                    val forums = forumsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    forums?.forEach{ forum ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.author?.id,
                                forum.author?.name,
                                forum.author?.username,
                                forum.author?.email,
                                forum.author?.role,
                                forum.author?.tanggalLahir,
                                forum.author?.image,
                                forum.title,
                                forum.description,
                                forum.favoriteCount,
                                forum.commentCount,
                                forum.isFavorite
                            )
                        )
                    }
                    Log.e("Pagination", "Forum size: ${forumModels.size}")
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ForumFavResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun onItemClick(position: Int) {
        val forumId = forumModels[position].id
        val intent = Intent(this, ForumRayaKomentarActivity::class.java)
        intent.putExtra("idForum", forumId)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}