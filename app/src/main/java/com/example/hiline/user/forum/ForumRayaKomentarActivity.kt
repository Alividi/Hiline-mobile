package com.example.hiline.user.forum

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ForumCommentUserAdapter
import com.example.hiline.service.ForumService
import com.example.hiline.model.CommentModel
import com.example.hiline.model.ForumModel
import com.example.hiline.request.CommentRequest
import com.example.hiline.response.CommentResponse
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ForumResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.response.KomenResponse
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaKomentarActivity : AppCompatActivity() {

    private lateinit var commentModels: ArrayList<CommentModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumCommentUserAdapter
    private lateinit var prefManager: PrefManager
    private lateinit var ivPPForum: ImageView
    private lateinit var tvNamaForum: TextView
    private lateinit var tvUsernameForum: TextView
    private lateinit var tvJudulForum: TextView
    private lateinit var tvIsiForum: TextView
    private lateinit var checkboxFav: CheckBox
    private lateinit var tvFavCount: TextView
    private lateinit var tvReplies: TextView
    private lateinit var etReply: EditText
    private lateinit var ivSend: ImageView
    private lateinit var idForum: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_komentar)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        ivPPForum = findViewById(R.id.ivPPForum)
        tvNamaForum = findViewById(R.id.tvNamaForum)
        tvUsernameForum = findViewById(R.id.tvUsernameForum)
        tvJudulForum = findViewById(R.id.tvJudulForum)
        tvIsiForum = findViewById(R.id.tvIsiForum)
        checkboxFav = findViewById(R.id.checkboxFav)
        tvFavCount = findViewById(R.id.tvFavCount)
        tvReplies = findViewById(R.id.tvReplies)
        etReply = findViewById(R.id.etReply)
        ivSend = findViewById(R.id.ivSend)

        idForum = intent.getStringExtra("idForum").toString()

        commentModels = ArrayList()
        getForumById()
        rvForumRaya = findViewById(R.id.rvForumRaya)
        adapter = ForumCommentUserAdapter(this, this,commentModels)
        rvForumRaya.adapter = adapter
        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaKomentarActivity)

        ivSend.setOnClickListener {
            if (etReply.text.toString() == ""){
                etReply.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etReply, InputMethodManager.SHOW_IMPLICIT)
            } else{
                createComment()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etReply.windowToken, 0)
                etReply.text.clear()
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, ForumRayaActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun getForumById(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.getForumById(idForum,aToken)
        call.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val comments = forumsResponse?.data?.comments

                    val imgUri = forums?.author?.image

                    if (!imgUri.isNullOrEmpty()) {
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPPForum)
                    } else {
                        ivPPForum.setImageResource(R.drawable.pp_admin)
                    }

                    tvNamaForum.text = forums?.author?.name
                    tvUsernameForum.text = "@" + forums?.author?.username
                    tvJudulForum.text = forums?.title
                    tvIsiForum.text = forums?.description
                    tvFavCount.text = forums?.favoriteCount.toString()
                    checkboxFav.isChecked = forums?.isFavorite == true
                    tvReplies.text = "${tvReplies.text}(${forums?.commentCount.toString()})"

                    checkboxFav.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            favForum(forums?.id.toString())
                            val newLikeCount = forums?.favoriteCount?.plus(1)
                            tvFavCount.text = newLikeCount.toString()
                        } else {
                            favForum(forums?.id.toString())
                            val newLikeCount = forums?.favoriteCount?.minus(1)
                            tvFavCount.text = newLikeCount.toString()
                        }
                    }

                    comments?.forEach { comment ->
                        commentModels.add(
                            CommentModel(
                                comment.id,
                                comment.user?.id,
                                comment.user?.name,
                                comment.user?.username,
                                comment.user?.email,
                                comment.user?.role,
                                comment.user?.tanggalLahir,
                                comment.user?.image,
                                comment.user?.point?.grade,
                                comment.comment,
                                comment.likeCount,
                                comment.isLike,
                                comment.isMe
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaKomentarActivity)
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")

                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun createComment(){
        val request = CommentRequest()
        request.comment = etReply.text.toString()

        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.createComment(idForum,aToken,request)
        call.enqueue(object : Callback<KomenResponse> {
            override fun onResponse(call: Call<KomenResponse>, response: Response<KomenResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val newCommentModel: ArrayList<CommentModel> = ArrayList()
                    val comments = response.body()?.data
                    comments?.let {newCommentModel.add(
                        CommentModel(
                            comments.id,
                            prefManager.getId(),
                            prefManager.getNama(),
                            prefManager.getUsername(),
                            prefManager.getEmail(),
                            prefManager.getRole(),
                            prefManager.getTanggal(),
                            comments.user?.image,
                            comments.user?.point?.grade,
                            comments.comment,
                            comments.likeCount,
                            false,
                            true
                        )
                    )
                        adapter.updateComments(newCommentModel)
                        rvForumRaya.scrollToPosition(commentModels.size - 1)
                    }
                } else {
                val rawResponse = response.raw().toString()
                Log.e("Raw Response: ", rawResponse)
                Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<KomenResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun favForum(id: String){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.favForum(id,aToken)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, ForumRayaActivity::class.java)
        startActivity(intent)
        finish()
    }

}
