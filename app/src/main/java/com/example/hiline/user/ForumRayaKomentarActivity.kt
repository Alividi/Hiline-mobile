package com.example.hiline.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.ForumCommentUserAdapter
import com.example.hiline.api.ForumApi
import com.example.hiline.model.CommentModel
import com.example.hiline.model.CommentRequest
import com.example.hiline.model.CommentResponse
import com.example.hiline.model.ForumResponse
import com.example.hiline.model.ForumsResponse
import com.squareup.picasso.Picasso
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
        getComments()
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
                postComment()
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

    fun getComments(){
        val id = idForum
        val token = "Bearer ${prefManager.getAccessToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.getForum(id,token)
        call.enqueue(object : Callback<ForumResponse> {
            override fun onResponse(call: Call<ForumResponse>, response: Response<ForumResponse>) {
                if (response.isSuccessful) {
                    val forumsResponse = response.body()
                    val forums =  forumsResponse?.data
                    val comments = forums?.comment
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    val imgUri = forums?.user?.profile_image

                    if (!imgUri.isNullOrEmpty()) {
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPPForum)
                    } else {
                        ivPPForum.setImageResource(R.drawable.pp_admin)
                    }

                    tvNamaForum.text = forums?.user?.nama
                    tvUsernameForum.text = "@" + forums?.user?.username
                    tvJudulForum.text = forums?.title
                    tvIsiForum.text = forums?.description
                    tvFavCount.text = forums?.favorite_count.toString()
                    checkboxFav.isChecked = forums?.is_favorite == true
                    tvReplies.text = "${tvReplies.text}(${forums?.comment_count.toString()})"

                    checkboxFav.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            favForum(forums?.id.toString())
                            val newLikeCount = forums?.favorite_count?.plus(1)
                            tvFavCount.text = newLikeCount.toString()
                        } else {
                            favForum(forums?.id.toString())
                            val newLikeCount = forums?.favorite_count?.minus(1)
                            tvFavCount.text = newLikeCount.toString()
                        }
                    }

                    comments?.forEach {comment: ForumResponse.datas.comments ->
                        commentModels.add(
                            CommentModel(
                                comment.id,
                                comment.user?.id,
                                comment.user?.nama,
                                comment.user?.username,
                                comment.user?.email,
                                comment.user?.role,
                                comment.user?.tanggal_lahir,
                                comment.user?.profile_image,
                                comment.message,
                                comment.like_count,
                                comment.liked,
                                comment.is_me
                            )
                        )
                        rvForumRaya.adapter = adapter
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaKomentarActivity)
                    }
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }

            override fun onFailure(call: Call<ForumResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun postComment(){
        val request = CommentRequest()
        request.postingan_id = intent.getStringExtra("idForum").toString()
        request.message = etReply.text.toString()

        val retro = Retro().getRetroClientInstance().create(ForumApi::class.java)
        val tokenAuth = "Bearer ${prefManager.getAccessToken()}"

        retro.postComment(tokenAuth, request).enqueue(object : Callback<CommentResponse>{
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val newCommentModel: ArrayList<CommentModel> = ArrayList()
                    val comments = response.body()?.data
                    comments?.let {
                        newCommentModel.add(
                            CommentModel(
                                comments.id,
                                prefManager.getId(),
                                prefManager.getNama(),
                                prefManager.getUsername(),
                                prefManager.getEmail(),
                                prefManager.getRole(),
                                prefManager.getTanggal(),
                                prefManager.getPImg(),
                                comments.message,
                                comments.like_count,
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
                    Log.e("Error Code: ", response.code().toString() + response.message().toString())
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun favForum(id: String){
        val token = "Bearer ${prefManager.getAccessToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)
        val call = forumApi.favForum(id,token)

        call.enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(
                call: Call<ForumsResponse>,
                response: Response<ForumsResponse>
            ) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response unsuccessful : ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }
            }
            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, ForumRayaActivity::class.java)
        startActivity(intent)
        finish()
    }

}
