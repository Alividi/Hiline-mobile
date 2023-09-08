package com.example.hiline.admin

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
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.adapter.ForumCommentAdminAdapter
import com.example.hiline.api.ForumApi
import com.example.hiline.model.CommentModel
import com.example.hiline.model.ForumResponse
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

                    tvJudulForum.text = forums?.title
                    tvIsiForum.text = forums?.description
                    tvBalasan.text = "${tvBalasan.text}(${forums?.comment_count.toString()})"

                    ivMore.setOnClickListener {
                        showMore(forums?.title.toString(), forums?.description.toString())
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
                        rvForumRaya.layoutManager = LinearLayoutManager(this@ForumRayaInfoActivity)
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
        val token = "Bearer ${prefManager.getAccessToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.deleteForum(id,token)
        call.enqueue(object : Callback<ForumResponse> {
            override fun onResponse(call: Call<ForumResponse>, response: Response<ForumResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    val intent = Intent(this@ForumRayaInfoActivity, ForumRayaAdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<ForumResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, ForumRayaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}