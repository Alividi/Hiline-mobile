package com.example.hiline.admin

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.ForumRayaAdminAdapter
import com.example.hiline.api.ForumApi
import com.example.hiline.interfaces.ForumRayaInterface
import com.example.hiline.model.ForumModel
import com.example.hiline.model.ForumsResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaAdminActivity : AppCompatActivity(), ForumRayaInterface {

    private lateinit var forumModels: ArrayList<ForumModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumRayaAdminAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_admin)
        prefManager = PrefManager(this)

        val fab: FloatingActionButton = findViewById(R.id.fabTambah)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val svForumRaya: SearchView = findViewById(R.id.svForumRaya)

        svForumRaya.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }
        })

        forumModels = ArrayList()
        getForums()
        rvForumRaya = findViewById(R.id.rvForumRaya)
        adapter = ForumRayaAdminAdapter(this@ForumRayaAdminActivity,this,forumModels,this)

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

    fun getForums() {
        val token = "Bearer ${prefManager.getAccessToken()}"
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
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaAdminActivity)
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