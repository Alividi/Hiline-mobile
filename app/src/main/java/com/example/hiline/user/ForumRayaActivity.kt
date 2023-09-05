package com.example.hiline.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.ForumRayaUserAdapter
import com.example.hiline.api.ForumApi
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.model.ForumsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaActivity : AppCompatActivity(), ForumRayaInterface {

    private lateinit var forumModels: ArrayList<ForumModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumRayaUserAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvTerbaru: TextView = findViewById(R.id.tvTerbaru)
        val tvPopuler: TextView = findViewById(R.id.tvPopuler)
        val tvFavorite: TextView = findViewById(R.id.tvFavorite)

        forumModels = ArrayList()
        getForums()
        rvForumRaya = findViewById(R.id.rvForumRaya)
        adapter = ForumRayaUserAdapter(this, forumModels,this)

        tvTerbaru.setOnClickListener {
            tvTerbaru.setBackgroundResource(R.drawable.filter_selected_bg)
            tvTerbaru.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvPopuler.setBackgroundResource(R.drawable.filter_bg)
            tvPopuler.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFavorite.setBackgroundResource(R.drawable.filter_bg)
            tvFavorite.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            forumModels.clear()
            getForumsNew()
        }
        tvPopuler.setOnClickListener {
            tvPopuler.setBackgroundResource(R.drawable.filter_selected_bg)
            tvPopuler.setTextColor(ContextCompat.getColor(this, R.color.tufts_blue))
            tvTerbaru.setBackgroundResource(R.drawable.filter_bg)
            tvTerbaru.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            tvFavorite.setBackgroundResource(R.drawable.filter_bg)
            tvFavorite.setTextColor(ContextCompat.getColor(this, R.color.silver2))
            forumModels.clear()
            getForumsPopular()
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

    fun getForums() {
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.getForums(token)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    forums?.forEach{ forum: ForumsResponse.datas ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.user?.id,
                                forum.user?.nama,
                                forum.user?.username,
                                forum.user?.email,
                                forum.user?.role,
                                forum.user?.tanggal_lahir,
                                forum.user?.profile_image,
                                forum.title,
                                forum.description,
                                forum.favorite_count,
                                forum.comment_count,
                                forum.is_favorite
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaActivity)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun getForumsNew() {
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.getForumsNew(token)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    forums?.forEach{ forum: ForumsResponse.datas ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.user?.id,
                                forum.user?.nama,
                                forum.user?.username,
                                forum.user?.email,
                                forum.user?.role,
                                forum.user?.tanggal_lahir,
                                forum.user?.profile_image,
                                forum.title,
                                forum.description,
                                forum.favorite_count,
                                forum.comment_count,
                                forum.is_favorite
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaActivity)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun getForumsPopular(){
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.getForumsPopular(token)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    forums?.forEach{ forum: ForumsResponse.datas ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.user?.id,
                                forum.user?.nama,
                                forum.user?.username,
                                forum.user?.email,
                                forum.user?.role,
                                forum.user?.tanggal_lahir,
                                forum.user?.profile_image,
                                forum.title,
                                forum.description,
                                forum.favorite_count,
                                forum.comment_count,
                                forum.is_favorite
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaActivity)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun getForumsFavorite(){
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.getForumsFavorite(token)
        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    forums?.forEach{ forum: ForumsResponse.datas ->
                        forumModels.add(
                            ForumModel(
                                forum.id,
                                forum.user?.id,
                                forum.user?.nama,
                                forum.user?.username,
                                forum.user?.email,
                                forum.user?.role,
                                forum.user?.tanggal_lahir,
                                forum.user?.profile_image,
                                forum.title,
                                forum.description,
                                forum.favorite_count,
                                forum.comment_count,
                                forum.is_favorite
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaActivity)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
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