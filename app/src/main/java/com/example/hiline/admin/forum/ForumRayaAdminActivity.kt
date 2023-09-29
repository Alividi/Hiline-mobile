package com.example.hiline.admin.forum

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ForumRayaAdminAdapter
import com.example.hiline.adapter.forum.ForumRayaUserAdapter
import com.example.hiline.admin.MainAdminActivity
import com.example.hiline.service.ForumService
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.response.ForumsResponse
import com.example.hiline.service.TokenAuthenticator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaAdminActivity : AppCompatActivity(), ForumRayaInterface {

    private lateinit var forumModels: ArrayList<ForumModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumRayaAdminAdapter
    private lateinit var prefManager: PrefManager
    private var totalPage = 1
    private var currentPage = 1
    private val LOAD_MORE_THRESHOLD = 1
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private var keyword:String = ""
    private lateinit var svForumRaya: SearchView
    private val handler = Handler()
    private val apiCallRunnable = Runnable { performApiCall() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_admin)
        prefManager = PrefManager(this)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val fab: FloatingActionButton = findViewById(R.id.fabTambah)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        svForumRaya = findViewById(R.id.svForumRaya)

        svForumRaya.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //filterList(newText)
                handler.removeCallbacks(apiCallRunnable)
                handler.postDelayed(apiCallRunnable, 300)
                return true
            }
        })

        forumModels = ArrayList()
        getForums(page = 1)
        rvForumRaya = findViewById(R.id.rvForumRaya)
        layoutManager = LinearLayoutManager(this@ForumRayaAdminActivity)
        rvForumRaya.layoutManager = layoutManager
        adapter = ForumRayaAdminAdapter(this@ForumRayaAdminActivity,this,forumModels,this)
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

        fab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

        btnBack.setOnClickListener {
            val intent = Intent(this, MainAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        fab.setOnClickListener{
            val intent = Intent(this, ForumRayaTambahActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performApiCall() {
        keyword = svForumRaya.query.toString()
        forumModels.clear()
        getForumsFilter(page = 1, keyword)
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
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data?.forumData
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
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    fun getForumsFilter(page: Int, keyword: String) {
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getForumsFilter(page,keyword,aToken)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data?.forumData
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
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun onItemClick(position: Int) {
        val forumId = forumModels[position].id
        val intent = Intent(this, ForumRayaInfoActivity::class.java)
        intent.putExtra("idForum", forumId)
        startActivity(intent)
        finish()
    }

    private fun filterList(text: String) {
        val filterArray = ArrayList<ForumModel>()
        for (forumModel in forumModels) {
            if (forumModel.title?.lowercase()?.contains(text.lowercase()) == true) {
                filterArray.add(forumModel)
            }
        }
        if (filterArray.isEmpty()) {
            Toast.makeText(this, "Pencarian tidak ada", Toast.LENGTH_SHORT).show()
        } else {
            adapter.setFilteredModels(filterArray)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}