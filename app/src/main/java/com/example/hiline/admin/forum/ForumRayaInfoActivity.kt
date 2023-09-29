package com.example.hiline.admin.forum

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.service.Retro
import com.example.hiline.adapter.forum.ForumCommentAdminAdapter
import com.example.hiline.service.ForumService
import com.example.hiline.model.CommentModel
import com.example.hiline.response.CommentResponse
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ForumResponse
import com.example.hiline.service.TokenAuthenticator
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumRayaInfoActivity : AppCompatActivity() {

    private lateinit var commentModels: ArrayList<CommentModel>
    private lateinit var rvForumRaya: RecyclerView
    private lateinit var adapter: ForumCommentAdminAdapter
    private lateinit var prefManager: PrefManager
    private lateinit var tvJudulForum: TextView
    private lateinit var tvIsiForum: TextView
    private lateinit var ivMore: ImageView
    private lateinit var tvBalasan: TextView
    private lateinit var idForum: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_raya_info)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        tvJudulForum = findViewById(R.id.tvJudulForum)
        tvIsiForum = findViewById(R.id.tvIsiForum)
        ivMore = findViewById(R.id.ivMore)
        tvBalasan = findViewById(R.id.tvBalasan)

        idForum = intent.getStringExtra("idForum").toString()

        commentModels = ArrayList()
        getComments()
        rvForumRaya = findViewById(R.id.rvForumRaya)
        adapter = ForumCommentAdminAdapter(this,this,commentModels)

        btnBack.setOnClickListener {
            val intent = Intent(this, ForumRayaAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getComments(){
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

                    tvJudulForum.text = forums?.title
                    tvIsiForum.text = forums?.description
                    tvBalasan.text = "${tvBalasan.text}(${forums?.commentCount.toString()})"

                    ivMore.setOnClickListener {
                        showMore(forums?.title.toString(), forums?.description.toString())
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
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaInfoActivity)
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

    fun showMore(title: String, description: String ){
        val dialog = Dialog(this, R.style.MaterialDialogSheet)
        dialog.setContentView(R.layout.dialog_more_comment_admin)
        val clEdit: ConstraintLayout = dialog.findViewById(R.id.clEdit)
        val clHapus: ConstraintLayout = dialog.findViewById(R.id.clHapus)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.MaterialDialogSheetAnimation
        dialog.show()

        clEdit.setOnClickListener {
            val intent = Intent(this, ForumRayaEditActivity::class.java)
            intent.putExtra("id", idForum)
            intent.putExtra("title", title)
            intent.putExtra("description", description)
            startActivity(intent)
            finish()
        }

        clHapus.setOnClickListener {
            dialog.dismiss()
            showDeleteDialog()
        }

    }

    fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        val tvTitle: TextView = dialog.findViewById(R.id.tvTitle)
        val tvKet: TextView = dialog.findViewById(R.id.tvKet)
        dialog.setTitle("Hapus Forum")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        tvTitle.text = "Hapus Topik?"
        tvKet.text = "Aksi ini tidak dapat dibatalkan dan akan dihilangkan dari topik."

        btnHapus.setOnClickListener {
            dialog.dismiss()
            deleteForum()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }
    fun deleteForum(){
        val id = idForum
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getRetroForumUrl(okHttpClient)
        val service = retrofit.create(ForumService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.deleteForum(id,aToken)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val intent = Intent(this@ForumRayaInfoActivity, ForumRayaAdminActivity::class.java)
                    startActivity(intent)
                    finish()
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
        val intent = Intent(this, ForumRayaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}