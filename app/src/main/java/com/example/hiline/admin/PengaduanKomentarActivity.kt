package com.example.hiline.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.Retro
import com.example.hiline.api.ForumApi
import com.example.hiline.model.CommentResponse
import com.example.hiline.user.ForumRayaActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaduanKomentarActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var idComment: String
    private lateinit var ivPPForum: ImageView
    private lateinit var ivMedal: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvIsi: TextView
    private lateinit var checkboxLike: CheckBox
    private lateinit var tvLiked: TextView
    private lateinit var cvPPForum: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaduan_komentar)
        prefManager = PrefManager(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnHapus: AppCompatButton = findViewById(R.id.btnHapus)
        cvPPForum = findViewById(R.id.cvPPForum)
        ivPPForum = findViewById(R.id.ivPPForum)
        ivMedal = findViewById(R.id.ivMedal)
        tvNama = findViewById(R.id.tvNama)
        tvUsername = findViewById(R.id.tvUsername)
        tvIsi = findViewById(R.id.tvIsi)
        checkboxLike = findViewById(R.id.checkboxLike)
        tvLiked = findViewById(R.id.tvLiked)
        idComment = intent.getStringExtra("idComment").toString()

        ViewCompat.setElevation(ivMedal,cvPPForum.elevation * 2)

        getComment()

        btnBack.setOnClickListener {
            val intent = Intent(this, PengaduanActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHapus.setOnClickListener{
            showDeleteDialog()
        }
    }

    fun getComment(){
        val id = idComment
        val token = "Bearer ${prefManager.getToken()}"
        val reportApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = reportApi.getComment(id,token)
        call.enqueue(object : Callback<CommentResponse>{
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                if (response.isSuccessful) {
                    val report = response.body()?.data
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    val imgUri = report?.user?.profile_image

                    if (!imgUri.isNullOrEmpty()) {
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPPForum)
                    } else {
                        ivPPForum.setImageResource(R.drawable.pp_comment)
                    }

                    tvNama.text = report?.user?.nama
                    tvUsername.text = "@"+report?.user?.username
                    tvIsi.text = report?.message
                    tvLiked.text = report?.like_count.toString()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }

    fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_komentar)
        val btnHapus: AppCompatButton = dialog.findViewById(R.id.btnHapus)
        val btnKembali: TextView = dialog.findViewById(R.id.btnKembali)
        dialog.setTitle("Hapus Balasan")
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        btnHapus.setOnClickListener {
            dialog.dismiss()
            deleteComment()
        }
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteComment(){
        val id = idComment
        val token = "Bearer ${prefManager.getToken()}"
        val forumApi = Retro().getRetroClientInstance().create(ForumApi::class.java)

        val call = forumApi.deleteComment(id,token)
        call.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("ID deleted:", response.body()?.data?.id.toString())
                    val intent = Intent(this@PengaduanKomentarActivity, PengaduanActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response unsuccessful : ", rawResponse)
                    Log.e("Code: ", response.code().toString()+response.message().toString())
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, PengaduanActivity::class.java)
        startActivity(intent)
        finish()
    }
}